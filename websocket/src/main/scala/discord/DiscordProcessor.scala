package discord

import discord.Entities._
import discord.messages.JsonConverter.RawMessage
import io.circe.syntax.EncoderOps
import io.circe.{parser, DecodingFailure, Encoder}
import util.zio.ZioRunner.AppEnv
import websocket.client.WsProcessor
import websocket.client.WsProcessor.{WS, WsContext}
import zio.duration.durationInt
import zio.logging.log
import zio.{Has, Queue, RIO, Schedule, Task, UIO, ULayer, ZIO, ZManaged}

object DiscordProcessor {

  case class DiscordContext(s: Int) extends WsContext

  trait Service extends WsProcessor.Service[DiscordContext, RawMessage]

  class DiscordProcessorImpl(token: String, out: Queue[DiscordContext => RawMessage])(val ws: WS)
    extends Service
    with discord.entities.JsonConverter {

    private def push[M <: Communication](msg: M)(implicit encoder: Encoder[M#Payload]): UIO[Unit] = push(_ => msg)

    private def push[M <: Communication](msg: DiscordContext => M)(implicit encoder: Encoder[M#Payload]): UIO[Unit] =
      out.offer(ctx => RawMessage.toRaw(msg(ctx))(encoder)).unit

    override def init: RIO[AppEnv, Unit] = ZIO.unit

    override def receive: RIO[AppEnv, RawMessage] =
      for {
        json <- ws.receiveText()
        _    <- log.info(s"receive: $json")
        raw  <- RIO.fromEither(parser.parse(json).flatMap(_.as[RawMessage]))
        _    <- log.info(s"parsed as: $raw")
      } yield raw

    override def send(context: DiscordContext): RIO[AppEnv, Unit] =
      for {
        msgs <- out.takeUpTo(1)
        _ <- ZIO.foreach_(msgs) { f =>
          for {
            raw  <- RIO(f(context))
            json <- Task(raw.asJson.deepDropNullValues)
            _    <- ws.sendText(json.noSpaces)
            _    <- log.info(s"send: ${json.spaces2}")
          } yield ()
        }
      } yield ()

    override def updateContext(context: DiscordContext): RawMessage => RIO[AppEnv, DiscordContext] = { msg =>
      for {
        _   <- log.info(s"update context: $context")
        s   <- RIO.succeed(msg.s.getOrElse(context.s))
        ctx <- RIO.succeed(context.copy(s = s))
        _   <- log.info(s"new context: $ctx")
      } yield ctx
    }

    override def handler(context: DiscordContext): RawMessage => RIO[AppEnv, Unit] =
      raw => {
        val task = for {
          msg <- RIO.fromEither(RawMessage.toCommunication(raw))
          _   <- pull(context)(msg)
        } yield ()
        task.ignore
      }

    private def pull(context: DiscordContext): Communication => RIO[AppEnv, Unit] = {
      case Messages.Hello(payload) =>
        heartbeat(payload.heartbeatInterval) *> identify.delay(3.seconds)
      case Messages.HeartbeatAck => heartbeatAck
      case msg                   => log.info(s"Undefined msg: $msg")
    }

    def heartbeat(interval: Int): RIO[AppEnv, Unit] = {
      val task = for {
        - <- push(ctx => Messages.Heartbeat(ctx.s))
      } yield ()
      log.info(s"heartbeat $interval") *> task
        .repeat(Schedule.spaced(interval.millis))
        .fork
        .unit
    }

    def heartbeatAck: RIO[AppEnv, Unit] =
      for {
        _ <- log.info("HeartbeatAck")
      } yield ()

    def identify: RIO[AppEnv, Unit] =
      for {
        _ <- log.info("identify")
        properties: Properties = Properties("win10", "twg bot", "pc")
        presence = PresenceUpdate(
          status = StatusType.Online,
          activities = Seq(
            Activity("Watching for you", ActivityType.Watching)
          )
        )
        intents: Intent = Intent.GUILDS |
          Intent.GUILD_MESSAGES |
          Intent.GUILD_MESSAGE_REACTIONS |
          Intent.GUILD_MESSAGE_TYPING

        identify = Entities.Identify(token, properties, intents, presence = Some(presence))
        _ <- push(Messages.Identify(identify))
      } yield ()
  }

  def live(token: String): ULayer[Has[WS => Service]] =
    ZManaged.fromEffect {
      for {
        out <- Queue.unbounded[DiscordContext => RawMessage]
        processor = (ws: WS) => new DiscordProcessorImpl(token, out)(ws)
      } yield processor
    }.toLayer
}
