package websocket.client

import sttp.client3.asynchttpclient.zio.{AsyncHttpClientZioBackend, sendR}
import sttp.client3.{UriContext, asWebSocketAlways, basicRequest}
import sttp.ws.WebSocket
import util.zio.ZioRunner
import zio.duration.durationInt
import zio.logging.log
import zio.{Queue, RIO, Ref, Schedule, ZIO}

object Client extends ZioRunner {

  type ENV[R] = ZIO[AppEnv, Throwable, R]
  type WS     = WebSocket[ENV]

  class WSHandler(queue: Queue[String], ws: WS) {

    def run: RIO[AppEnv, Unit] =
      for {
        //        _ <- produce.forever.fork
        refIsStop <- Ref.make(false)
        _ <- send("XXX").delay(3.seconds).repeat(Schedule.recurs(3) && Schedule.spaced(3.seconds)).fork
        _ <- send("STOP").delay(4.seconds).repeat(Schedule.recurs(3) && Schedule.spaced(4.seconds)).fork
        _ <- send("PING").delay(10.seconds).repeat(Schedule.recurs(5) && Schedule.spaced(10.seconds)).fork
        _ <- consume.flatMap(handler).forever
      } yield ()

    def handler(v: String) = v match {
      case "XXX"  => log.info("get XXX")
      case "YYY"  => log.info("get YYY")
      case "PING" => log.info("get PING") *> send("PONG")
      case msg    => log.warn(s"Unknown message: $msg")
    }

    def send(msg: String) =
      for {
        _ <- log.info(s"send: $msg")
        _ <- ws.sendText(msg)
      } yield ()

    private val produce = for {
      v <- queue.take
      _ <- log.info(s"take: $v")
      _ <- ws.sendText(v.toString)
    } yield ()

    private val consume = for {
      v <- ws.receiveText(true)
      _ <- log.info(s"receive: $v")
    } yield v
  }

  def connectAndHandle(handler: WS => WSHandler) = {
    val request = basicRequest
//      .get(uri"ws://127.0.0.1:3030/greeter")
      .get(uri"wss://echo.websocket.org")
      .response(asWebSocketAlways[ENV, Unit](handler(_).run))
    println(request.toCurl)
    sendR(request)
  }

  override val start: ZIO[AppEnv, Any, Any] = {
    val p = for {
      q <- Queue.bounded[String](100)
      handler = new WSHandler(q, _)
      _ <- connectAndHandle(handler)
    } yield ()
    val layer = AsyncHttpClientZioBackend.layer()
    p.provideSomeLayer[AppEnv](layer)
  }
}
