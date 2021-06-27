package websocket

import com.typesafe.config.{Config, ConfigFactory}
import discord.DiscordProcessor
import discord.DiscordProcessor.DiscordContext
import sttp.client3.asynchttpclient.zio.{AsyncHttpClientZioBackend, SttpClient, sendR}
import sttp.client3.{Response, UriContext, asWebSocketAlways, basicRequest}
import util.zio.ZioRunner
import util.zio.ZioRunner.AppEnv
import websocket.client.WsProcessor.{ENV, WS}
import zio.{Has, RIO, RLayer, ZIO, ZLayer}

object Discord extends ZioRunner {

  case class DiscordConfig(url: String, version: Int, token: String)
  val conf: Config = ConfigFactory.load

  val discordConfig: DiscordConfig = {
    val cfg = conf.getConfig("websocket.discord")
    DiscordConfig(
      url = cfg.getString("url"),
      version = cfg.getInt("version"),
      token = cfg.getString("token")
    )
  }

  override def start: ZIO[AppEnv, Any, Any] = {
    program.provideLayer(layer)
  }

  private lazy val layer: RLayer[AppEnv, AppEnv with SttpClient with Has[WS => DiscordProcessor.Service]] =
    ZLayer.requires[AppEnv] ++ AsyncHttpClientZioBackend.layer() ++ DiscordProcessor.live(discordConfig.token)

  private lazy val program = for {
    processor <- ZIO.service[WS => DiscordProcessor.Service]
    connect   <- connect(processor)
  } yield connect

  private def connect(processor: WS => DiscordProcessor.Service): RIO[SttpClient with AppEnv, Response[Unit]] = {

    val context = DiscordContext(0)

    val uri = uri"${discordConfig.url}"
      .addParam("v", discordConfig.version.toString)
      .addParam("encoding", "json")

    val request = basicRequest
      .header("Origin", "discord")
      .get(uri)
      .response(asWebSocketAlways[ENV, Unit](ws => processor(ws).run(context)))

    println(request.toCurl)

    sendR(request)
  }

}
