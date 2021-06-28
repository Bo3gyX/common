package websocket.client

import zio.RIO
import zio.logging.Logging

object Processor {

  trait Message
  trait Context

  trait Service[R, M <: Message] {
    def init: RIO[R, Unit]
    def receive: RIO[R, M]
    def send: RIO[R, Unit]
    def handler: M => RIO[R, Unit]

    def run: RIO[R with Logging, Unit] = {
      for {
        _  <- init
        f1 <- consume.forever.fork
        f2 <- produce.forever.fork
        _  <- f1.join
        _  <- f2.join
      } yield ()
    }

    private def consume: RIO[R, Unit] =
      for {
        msg <- receive
        _   <- handler(msg)
      } yield ()

    private def produce: RIO[R with Logging, Unit] =
      for {
        _ <- send
      } yield ()
  }

}
