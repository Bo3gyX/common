package discord

import discord.entities.Activity.ActivityType
import discord.entities._
import discord.gateway._
import io.circe.parser
import io.circe.syntax.EncoderOps
import util.zio.ZioRunner.AppEnv
import websocket.client.WsProcessor
import websocket.client.WsProcessor.WS
import zio.duration.durationInt
import zio.logging.log
import zio.{Has, Queue, RIO, Ref, Schedule, Task, UIO, ULayer, ZIO, ZManaged}

object DiscordProcessor {

  case class HeartbeatState(isWaitAck: Boolean, attempt: Int) {
    def withWaitAck(isWaitAck: Boolean): HeartbeatState = this.copy(isWaitAck = isWaitAck)
    def incAttempt: HeartbeatState                      = this.copy(attempt = attempt + 1)
    def reset: HeartbeatState                           = HeartbeatState.init
  }

  object HeartbeatState {
    def init: HeartbeatState = HeartbeatState(isWaitAck = false, 0)
  }

  case class DiscordState(seqNumber: Int, ready: Option[Ready], heartbeat: HeartbeatState) {
    def withSeqNumber(seqNumber: Int): DiscordState            = this.copy(seqNumber = seqNumber)
    def withReady(ready: Ready): DiscordState                  = this.copy(ready = Some(ready))
    def withHeartbeat(heartbeat: HeartbeatState): DiscordState = this.copy(heartbeat = heartbeat)
    def waitAck: DiscordState                                  = withHeartbeat(heartbeat.withWaitAck(true))
    def resetWaitAck: DiscordState                             = withHeartbeat(heartbeat.withWaitAck(false))
  }

  object DiscordState {
    def init: DiscordState = DiscordState(0, None, HeartbeatState.init)
  }

  trait Service extends WsProcessor.Service[Payload]

  class DiscordProcessorImpl(token: String, state: Ref[DiscordState], out: Queue[DiscordState => Payload])(val ws: WS)
    extends Service
    with gateway.JsonConverter {

    private def push(msg: Payload): UIO[Unit] = push(_ => msg)

    private def push(f: DiscordState => Payload): UIO[Unit] =
      out.offer(ctx => f(ctx)).unit

    override def init: RIO[AppEnv, Unit] = ZIO.unit

    override def receive: RIO[AppEnv, Payload] =
      for {
        json    <- ws.receiveText()
        _       <- log.info(s"receive: $json")
        parsed  <- Task(parser.parse(json).flatMap(_.as[Payload]))
        payload <- RIO.fromEither(parsed)
        _       <- log.info(s"parsed as: $payload")
      } yield payload

    override def send: RIO[AppEnv, Unit] =
      for {
        f       <- out.take
        state   <- state.get
        payload <- Task(f(state))
        json    <- Task(payload.asJson.deepDropNullValues)
        _       <- ws.sendText(json.noSpaces)
        _       <- log.info(s"send: ${json.spaces2}")
      } yield ()

    override def handler: Payload => RIO[AppEnv, Unit] =
      payload =>
        for {
          _ <- state.update(old => old.copy(seqNumber = payload.s.getOrElse(old.seqNumber)))
          _ <- pull(payload)
        } yield ()

    private def pull: Payload => RIO[AppEnv, Unit] = {
      case gateway.Hello(payload)         => hello(payload) *> identify.delay(1.seconds)
      case gateway.Heartbeat(payload)     => heartbeat(payload)
      case gateway.HeartbeatAck           => heartbeatAck
      case gateway.Ready(payload)         => ready(payload)
      case gateway.MessageCreate(payload) => messageCreate(payload)
      case payload                        => log.warn(s"Unsupported payload: $payload")
    }

    private def hello(hello: entities.Hello): RIO[AppEnv, Unit] = {
      val task = for {
        ctx <- state.get
        heartbeat = ctx.heartbeat
        _ <- heartbeat.isWaitAck match {
          case true if heartbeat.attempt > 2 =>
            log.error(s"no ACK response more than ${heartbeat.attempt} times")
          case true =>
            val updatedHeartbeat = heartbeat.incAttempt
            log.warn(s"no ACK response, attempt: ${updatedHeartbeat.attempt}") *>
              state.update(ctx => ctx.withHeartbeat(updatedHeartbeat)) *>
              push(gateway.Heartbeat(ctx.seqNumber))
          case _ =>
            state.update(_.waitAck) *>
              push(gateway.Heartbeat(ctx.seqNumber))
        }
      } yield ()
      log.info(s"hello: ${hello.heartbeatInterval}") *> task
        .repeat(Schedule.spaced(hello.heartbeatInterval.millis))
        .fork
        .unit
    }

    private def heartbeat(seqNumber: Int): RIO[AppEnv, Unit] =
      for {
        _ <- log.info(s"heartbeat: $seqNumber")
        _ <- push(gateway.HeartbeatAck)
      } yield ()

    private def heartbeatAck: RIO[AppEnv, Unit] =
      for {
        _ <- state.update(_.resetWaitAck)
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
        _ <- push(gateway.Identify(identify))
      } yield ()

    private def ready(ready: entities.Ready): RIO[AppEnv, Unit] =
      for {
        _ <- log.info(s"Ready: my name is ${ready.user.username}")
        _ <- state.update(old => old.copy(ready = Some(ready)))
      } yield ()

    private def messageCreate(message: entities.Message): RIO[AppEnv, Unit] =
      log.info(s"message of ${message.author.username}: ${message.content}")
  }

  def live(token: String): ULayer[Has[WS => Service]] =
    ZManaged.fromEffect {
      for {
        state <- Ref.make(DiscordState.init)
        out   <- Queue.unbounded[DiscordState => Payload]
        processor = (ws: WS) => new DiscordProcessorImpl(token, state, out)(ws)
      } yield processor
    }.toLayer
}
