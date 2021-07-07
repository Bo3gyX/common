package websocket.client

import sttp.ws.WebSocket
import util.zio.ZioRunner.AppEnv
import zio.RIO

object WsProcessor {

  type ENV[R] = RIO[AppEnv, R]
  type WS     = WebSocket[ENV]

  trait Service[M] extends Processor.Service[AppEnv, M] {
    def ws: WS
  }

}
