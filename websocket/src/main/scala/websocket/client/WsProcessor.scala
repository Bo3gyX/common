package websocket.client

import sttp.ws.WebSocket
import util.zio.ZioRunner.AppEnv
import zio.RIO

object WsProcessor {

  type ENV[R] = RIO[AppEnv, R]
  type WS     = WebSocket[ENV]

  trait WsMessage extends Processor.Message

  trait Service[M <: WsMessage] extends Processor.Service[AppEnv, M] {
    def ws: WS
  }

}
