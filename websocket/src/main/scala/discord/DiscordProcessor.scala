package discord

import discord.entities.Activity.ActivityType
import discord.entities._
import discord.gateway.{Event, Operation, Payload}
import io.circe.syntax.EncoderOps
import io.circe.{parser, Error}
import util.zio.ZioRunner.AppEnv
import websocket.client.WsProcessor
import websocket.client.WsProcessor.WS
import zio.duration.durationInt
import zio.logging.log
import zio.{Has, Queue, RIO, Ref, Schedule, Task, UIO, ULayer, ZIO, ZManaged}

object DiscordProcessor {

  case class DiscordState(seqNumber: Int)

  trait Service extends WsProcessor.Service[Payload] {
    def token: String
  }

  class DiscordProcessorImpl(
      val token: String,
      state: Ref[DiscordState],
      out: Queue[DiscordState => Payload]
    )(val ws: WS)
    extends Service
    with gateway.JsonConverter {

    register(Operation.Hello)
    register(Operation.Heartbeat)
    register(Operation.HeartbeatAck)
    register(Operation.Identify)
    register(Event.MessageCreate)

    private def push(msg: Payload): UIO[Unit] = push(_ => msg)

    private def push(f: DiscordState => Payload): UIO[Unit] =
      out.offer(ctx => f(ctx)).unit

    override def init: RIO[AppEnv, Unit] = ZIO.unit

    override def receive: RIO[AppEnv, Payload] =
      for {
        json   <- ws.receiveText()
        _      <- log.info(s"receive: $json")
        parsed <- Task(parser.parse(json).flatMap(_.as[Payload]))
//          .flatMapError(err =>
//          for {
//            _ <- log.throwable("parse error", err)
//          } yield err
//        )
        raw <- RIO.fromEither(parsed)
        _   <- log.info(s"parsed as: $raw")
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

    override def handler: Payload => RIO[AppEnv, Unit] =
      raw =>
        (for {
          _ <- log.info(s"pull: $raw")
          _ <- pull(raw)
        } yield ()).catchSome {
          case e: Error => log.throwable("handler error", e)
        }.ignore

    private def pull: Payload => RIO[AppEnv, Unit] = {
      case Payload.Raw(_, Some(Hello(heartbeatInterval)), _, _) =>
        heartbeat(heartbeatInterval) *> identify.delay(1.seconds)
      case Payload.Raw(Operation.HeartbeatAck, _, _, _) => heartbeatAck
      case Payload.Raw(_, Some(message: Message), _, _) =>
        log.info(s"message from ${message.author.username}: ${message.content}")
      case msg => log.warn(s"Undefined msg: $msg")
    }

    private def heartbeat(interval: Int): RIO[AppEnv, Unit] = {
      val task = for {
        _ <- push(ctx => Payload(Operation.Heartbeat, ctx.seqNumber))
      } yield ()
      log.info(s"heartbeat $interval") *> task
        .repeat(Schedule.spaced(interval.millis))
        .fork
        .unit
    }

    private def heartbeatAck: RIO[AppEnv, Unit] =
      for {
        _ <- log.info("HeartbeatAck")
      } yield ()

    private def identify: RIO[AppEnv, Unit] =
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
        identify        = entities.Identify(token, properties, intents, presence = Some(presence))
        _ <- push(Payload(Operation.Identify, identify))
      } yield ()

  }

  def live(token: String): ULayer[Has[WS => Service]] =
    ZManaged.fromEffect {
      for {
        state <- Ref.make(DiscordState(0))
        out   <- Queue.unbounded[DiscordState => Payload]
        processor = (ws: WS) => new DiscordProcessorImpl(token, state, out)(ws)
      } yield processor
    }.toLayer
}
