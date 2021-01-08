package common.zioapp

import zio._
import zio.duration.durationInt
import zio.logging.slf4j._
import zio.logging.{log, LogAnnotation, Logging}

trait ZioRunner extends App {

  type AppEnv = ZEnv with Logging

  def start: ZIO[AppEnv, Any, Any]

  def logBlock[R, E, A](zio: ZIO[R, E, A]): ZIO[Logging with R, E, A] = {
    log.locally(LogAnnotation.CorrelationId(Some(java.util.UUID.randomUUID())))(zio)
  }

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = {
    val eff = for {
      _ <- log.info("app run")
      _ <- processing.raceFirst(timeout)
      _ <- log.info("app finish")
    } yield ()

    eff.provideCustomLayer(loggingLayer).exitCode
  }

  private def processing = {
    for {
      res <- start.foldM(failure, success)
      _   <- log.info("process finish")
    } yield res
  }

  private def timeout = {
    log.info("timeout finish").delay(120.seconds)
  }

  private def failure[T](error: T) = {
    error match {
      case err: Throwable =>
        log.error(s"Throwable: ${err.getMessage}")
      case err =>
        log.error(s"Error: $err")
    }
  }

  private def success[T](a: T) = {
    log.info(s"Complete: $a")
  }

  private val loggingLayer = {
    Slf4jLogger.make { (context, message) =>
      if (context.get(LogAnnotation.CorrelationId).nonEmpty)
        "[correlation-id = %s] %s".format(context(LogAnnotation.CorrelationId), message)
      else message
    }
  }

}
