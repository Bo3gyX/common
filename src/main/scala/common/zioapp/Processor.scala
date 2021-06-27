package common.zioapp

import common.ZioApp.AppEnv
import common.zioapp.MyProcessor.MyContext
import zio.{Has, Queue, RIO, Ref, ZLayer, ZManaged}

object Processor {

  trait Message
  trait Context

  trait Service[R, C <: Context] {
    def init: RIO[R, Unit]
    def receive: RIO[R, Message]
    def send: RIO[R, Unit]
    def updateContext(context: C): Message => RIO[R, C]
    def handler(context: C): Message => RIO[R, Unit]

    def run(context: C): RIO[R, Unit] = {
      for {
        _      <- init
        refCtx <- Ref.make(context)
        f1     <- consume(refCtx).forever.fork
        f2     <- produce.forever.fork
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

    private def produce: RIO[R, Unit] =
      for {
        _ <- send
      } yield ()
  }

  type ProcessorService = Processor.Service[AppEnv, MyContext]

  val live: ZLayer[AppEnv, Throwable, Has[ProcessorService]] =
    ZManaged
      .fromEffect(
        for {
          in  <- Queue.unbounded[Message]
          out <- Queue.unbounded[Message]
        } yield new MyProcessor(in, out)
      )
      .toLayer
}
