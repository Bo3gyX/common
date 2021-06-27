package websocket.client

import sttp.ws.WebSocket
import util.zio.ZioRunner.AppEnv
import zio.RIO

object WsProcessor {

  type ENV[R] = RIO[AppEnv, R]
  type WS     = WebSocket[ENV]

  trait WsMessage extends Processor.Message
  trait WsContext extends Processor.Context

  trait Service[C <: WsContext, M <: WsMessage] extends Processor.Service[AppEnv, C, M] {
    def ws: WS
  }

}
