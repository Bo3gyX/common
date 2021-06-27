package websocket.client

import com.typesafe.config.{Config, ConfigFactory}
import discord.Entities._
import discord.messages.JsonConverter._
import discord.{Entities, Message, Messages}
import io.circe.parser
import io.circe.syntax.EncoderOps
import sttp.client3.asynchttpclient.zio.{AsyncHttpClientZioBackend, sendR}
import sttp.client3.{UriContext, asWebSocketAlways, basicRequest}
import sttp.ws.WebSocket
import util.zio.ZioRunner
import util.zio.ZioRunner.AppEnv
import zio.duration.durationInt
import zio.logging.log
import zio.{Schedule, Task, ZIO}

object DiscordClient extends ZioRunner {

  val conf: Config = ConfigFactory.load

  val version = conf.getString("websocket.discord.version")
  val token   = conf.getString("websocket.discord.token")
  val url     = conf.getString("websocket.discord.url")

  type ENV[R]  = ZIO[AppEnv, Throwable, R]
  type WS      = WebSocket[ENV]
  type Handler = WS => ENV[Unit]

  def handler(ws: WS): ENV[Unit] = {

    def convert(message: String) =
      for {
        json <- ZIO.fromEither(parser.parse(message))
        msg  <- ZIO.fromEither(json.as[Message])
      } yield msg

    def process(message: Message): ENV[Unit] = message match {
      case Messages.Hello(payload) =>
        for {
          _ <- heartbeat(payload.heartbeatInterval)
          _ <- identify
        } yield ()

      case msg => log.info(s"process: $msg")
    }

    def send(message: Message): ENV[Unit] = {
      for {
        _    <- log.info(s"send $message")
        json <- Task(message.asJson.deepDropNullValues.noSpaces)
        res  <- ws.sendText(json)
      } yield res
    }

    def identify = {
      val properties: Properties = Properties("ios", "twg bot", "mac book pro")
      val presence = PresenceUpdate(
        StatusType.Online,
        Seq(
          Activity("Watching for you", ActivityType.Watching)
        )
      )

      val intents: Intent =
        Intent.GUILDS |
          Intent.GUILD_MESSAGES |
          Intent.GUILD_MESSAGE_REACTIONS |
          Intent.GUILD_MESSAGE_TYPING

      val identify = Entities.Identify(token, properties, intents)
      send(Messages.Identify(identify))
    }

    def heartbeat(interval: Int): ENV[Unit] = {
      val task = for {
        _ <- log.info(s"heartbeat $interval")
        _ <- send(Messages.Heartbeat(0))
      } yield ()
      task
        .delay(interval.millis)
        .repeat(Schedule.spaced(interval.millis))
        .fork
        .unit
    }

    (for {
      txt <- ws.receiveText(true)
      _   <- log.info(s"receiveText $txt")
      msg <- convert(txt)
      _   <- process(msg)
    } yield ()).forever
  }

  private def connect(handler: Handler) = {

    val uri = uri"wss://gateway.discord.gg"
      .addParam("v", "9")
      .addParam("encoding", "json")

    val request = basicRequest
    //      .header(Header.authorization("Bot", token))
      .header("Origin", "discord")
      .get(uri)
      .response(asWebSocketAlways[ENV, Unit](handler))
    println(request.toCurl)
    sendR(request)
  }

  override val start: ZIO[AppEnv, Any, Any] = {
    val p = for {
      r <- connect(handler)
    } yield r
    val layer = AsyncHttpClientZioBackend.layer()
    p.provideSomeLayer[AppEnv](layer)
  }
}
