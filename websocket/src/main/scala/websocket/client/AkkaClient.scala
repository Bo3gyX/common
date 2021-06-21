package websocket.client

import akka.Done
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.http.scaladsl.server.directives.DebuggingDirectives
import akka.stream.scaladsl.{Keep, Sink, Source}
import util.AkkaApp

import scala.concurrent.Future

object AkkaClient extends AkkaApp("WebSocketClient") {

  val incoming: Sink[Message, Future[Done]] =
    Sink.foreach[Message] {
      case message: TextMessage.Strict =>
        println(message.text)
      case _ =>
      // ignore other message types
    }

  // send this as a message over the WebSocket
  val outgoing = Source.single(TextMessage("hello world!"))

  // flow to use (note: not re-usable!)
  val webSocketFlow = Http().webSocketClientFlow(WebSocketRequest("wss://gateway.discord.gg/?v=9&encoding=json"))

  // the materialized value is a tuple with
  // upgradeResponse is a Future[WebSocketUpgradeResponse] that
  // completes or fails when the connection succeeds or fails
  // and closed is a Future[Done] with the stream completion from the incoming sink
  val (upgradeResponse, closed) =
    outgoing
      .viaMat(webSocketFlow)(Keep.right) // keep the materialized Future[WebSocketUpgradeResponse]
      .toMat(incoming)(Keep.both) // also keep the Future[Done]
      .run()

  upgradeResponse.foreach(println)

  // just like a regular http request we can access response status which is available via upgrade.response.status
  // status code 101 (Switching Protocols) indicates that server support WebSockets
  val connected = upgradeResponse.flatMap { upgrade =>
    if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
      Future.successful(Done)
    } else {
      throw new RuntimeException(s"Connection failed: ${upgrade.response.status}")
    }
  }

  // in a real application you would not side effect here
  connected.onComplete(println)
  closed.foreach(_ => println("closed"))
}
