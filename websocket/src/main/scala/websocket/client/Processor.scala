package websocket.client

import zio.{RIO, Ref}

object Processor {

  trait Message
  trait Context

  trait Service[R, C <: Context, M <: Message] {
    def init: RIO[R, Unit]
    def receive: RIO[R, M]
    def send(context: C): RIO[R, Unit]
    def updateContext(context: C): M => RIO[R, C]
    def handler(context: C): M => RIO[R, Unit]

    def run(context: C): RIO[R, Unit] = {
      for {
        _      <- init
        refCtx <- Ref.make(context)
        f1     <- consume(refCtx).forever.fork
        f2     <- produce(refCtx).forever.fork
        _      <- f1.join
        _      <- f2.join
      } yield ()
    }

    private def consume(refCtx: Ref[C]): RIO[R, Unit] =
      for {
        msg         <- receive
        currContext <- refCtx.get
        newContext  <- updateContext(currContext)(msg)
        _           <- refCtx.set(newContext)
        _           <- handler(newContext)(msg)
      } yield ()

    private def produce(refCtx: Ref[C]): RIO[R, Unit] =
      for {
        ctx <- refCtx.get
        _   <- send(ctx)
      } yield ()
  }

}
