package discord

import discord.entities.Activity.ActivityType
import discord.entities._
import discord.messages.RawMessage.toRaw
import discord.messages.{GatewaysMessage, GatewaysMessages, RawMessage}
import io.circe.syntax.EncoderOps
import io.circe.{parser, DecodingFailure, Encoder, Error}
import util.zio.ZioRunner.AppEnv
import websocket.client.WsProcessor
import websocket.client.WsProcessor.WS
import zio.duration.durationInt
import zio.logging.log
import zio.{Has, Queue, RIO, Ref, Schedule, Task, UIO, ULayer, ZIO, ZManaged}

object DiscordProcessor {

  case class DiscordState(seqNumber: Int)

  trait Service extends WsProcessor.Service[RawMessage] {
    def token: String
  }

  class DiscordProcessorImpl(
      val token: String,
      state: Ref[DiscordState],
      out: Queue[DiscordState => RawMessage]
    )(val ws: WS)
    extends Service
    with messages.JsonConverter {

    private def push[M <: GatewaysMessage](msg: M)(implicit encoder: Encoder[M#Payload]): UIO[Unit] = push(_ => msg)

    private def push[M <: GatewaysMessage](msg: DiscordState => M)(implicit encoder: Encoder[M#Payload]): UIO[Unit] =
      out.offer(ctx => toRaw(msg(ctx))(encoder)).unit

    override def init: RIO[AppEnv, Unit] = ZIO.unit

    override def receive: RIO[AppEnv, RawMessage] =
      for {
        json  <- ws.receiveText()
        _     <- log.info(s"receive: $json")
        raw   <- RIO.fromEither(parser.parse(json).flatMap(_.as[RawMessage]))
        _     <- log.info(s"parsed as: $raw")
        state <- state.updateAndGet(state => raw.s.map(s => state.copy(seqNumber = s)).getOrElse(state))
        _     <- log.info(s"update state to $state")
      } yield raw

    override def send: RIO[AppEnv, Unit] =
      for {
        f     <- out.take
        state <- state.get
        raw   <- Task(f(state))
        json  <- Task(raw.asJson.deepDropNullValues)
        _     <- ws.sendText(json.noSpaces)
        _     <- log.info(s"send: ${json.spaces2}")
      } yield ()

    override def handler: RawMessage => RIO[AppEnv, Unit] =
      raw => {
        val task = for {
          msg <- RIO.fromEither(RawMessage.toGatewaysMessage(raw))
          _   <- log.info(s"pull: $msg")
          _   <- pull(msg)
        } yield ()
        task.catchSome {
          case e: Error => log.throwable("handler error", e)
        }.ignore
      }

    private def pull: GatewaysMessage => RIO[AppEnv, Unit] = {
      case GatewaysMessages.Hello(payload)   => heartbeat(payload.heartbeatInterval) *> identify.delay(1.seconds)
      case GatewaysMessages.HeartbeatAck     => heartbeatAck
      case GatewaysMessages.Message(payload) => log.info(s"message from ${payload.author.username}: ${payload.content}")
      case msg                               => log.warn(s"Undefined msg: $msg")
    }

    def heartbeat(interval: Int): RIO[AppEnv, Unit] = {
      val task = for {
        - <- push(ctx => GatewaysMessages.Heartbeat(ctx.seqNumber))
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
        intents: Intent = Intent.GUILD_MESSAGES

        identify = entities.Identify(token, properties, intents, presence = Some(presence))
        _ <- push(GatewaysMessages.Identify(identify))
      } yield ()
  }

  def live(token: String): ULayer[Has[WS => Service]] =
    ZManaged.fromEffect {
      for {
        state <- Ref.make(DiscordState(0))
        out   <- Queue.unbounded[DiscordState => RawMessage]
        processor = (ws: WS) => new DiscordProcessorImpl(token, state, out)(ws)
      } yield processor
    }.toLayer
}
