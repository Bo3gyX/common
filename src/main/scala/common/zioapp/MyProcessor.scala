package common.zioapp

import common.ZioApp
import common.ZioApp.AppEnv
import common.zioapp.MyProcessor._
import common.zioapp.Processor.{Context, Message}
import zio.duration.durationInt
import zio.logging.{log, Logging}
import zio.{Queue, RIO, Schedule, ZIO}

class MyProcessor(in: Queue[Message], out: Queue[Message]) extends Processor.Service[AppEnv, MyContext] {

  override def init: RIO[ZioApp.AppEnv, Unit] =
    in.offer(Ping).repeat(Schedule.spaced(5.seconds)).fork.unit

  override def receive: RIO[ZioApp.AppEnv, Message] =
    for {
      msg <- in.take
      _   <- log.info(s"receive $msg")
    } yield msg

  override def send: RIO[ZioApp.AppEnv, Unit] =
    for {
      msg <- out.take
      _   <- log.info(s"send: $msg")
    } yield ()

  override def updateContext(context: MyContext): Message => RIO[ZioApp.AppEnv, MyContext] =
    _ => ZIO.succeed(context.copy(cnt = context.cnt + 1))

  override def handler(context: MyContext): Message => RIO[ZioApp.AppEnv, Unit] = {
    case Question(value) => push(Answer(s"answer for question '$value'"))
    case Ping            => push(Pong(context.cnt))
    case _               => push(Unknown)
  }

  private def push(msg: Message): RIO[Logging, Unit] =
    for {
      res <- out.offer(msg)
      _   <- log.info(s"push $msg. $res")
    } yield ()
}

object MyProcessor {

  case class MyContext(cnt: Int) extends Context

  case class Question(question: String) extends Message
  case class Answer(answer: String)     extends Message
  case object Ping                      extends Message
  case class Pong(n: Int)               extends Message
  case object Unknown                   extends Message
  case object Start                     extends Message
  case object Stop                      extends Message
}
