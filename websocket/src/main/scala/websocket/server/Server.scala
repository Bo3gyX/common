package websocket.server

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.server.Directives.{handleWebSocketMessages, path}
import akka.stream.scaladsl.{Flow, Sink}
import util.AkkaApp

object Server extends AkkaApp("WebSocketServer") {

  def greeter: Flow[Message, Message, Any] =
    Flow[Message].mapConcat {
      case tm: TextMessage =>
        TextMessage(tm.textStream) :: Nil
      case bm: BinaryMessage =>
        // ignore binary messages but drain content to avoid the stream being clogged
        bm.dataStream.runWith(Sink.ignore)
        Nil
    }

  val websocketRoute =
    path("greeter") {
      handleWebSocketMessages(greeter)
    }


  val bindingFuture = Http().newServerAt(
    "localhost", port = 3030).bind(websocketRoute)

  bindingFuture.foreach(e => println(s"bind: ${e.localAddress}"))

  onStop(bindingFuture.flatMap(_.unbind()))
}
