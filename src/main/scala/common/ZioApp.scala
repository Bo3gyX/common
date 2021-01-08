package common

import common.zioapp.ZioRunner
import zio.clock.Clock
import zio.logging.{log, Logging}
import zio.{UIO, _}

import java.io.IOException
import scala.io.Source

object ZioApp extends ZioRunner {

  override def start = {
    val eff = ZIO.infinity
    eff
  }

  trait Loader[R] {
    def load(v: String): ZIO[R, IOException, String]
  }

  trait LoggerLoader[R] extends Loader[R with Logging] {

    abstract override def load(v: String): ZIO[R with Logging, IOException, String] =
      log.info("load") *> super.load(v)
  }

  trait RetryLoader[R] extends Loader[R with Clock] {

    abstract override def load(v: String): ZIO[R with Clock, IOException, String] =
      super.load(v).retry(Schedule.recurs(3))
  }

  class StringLoader[R] extends Loader[R] {

    override def load(v: String): ZIO[R, IOException, String] = {
      Managed.make(open(v))(close).use(use).mapError(mapError)
    }

    private def open(name: String): Task[Source] = Task(Source.fromFile(name))
    private def close(s: Source): UIO[Unit]      = UIO(s.close())
    private def use(s: Source): Task[String]     = Task(s.getLines().mkString)

    private def mapError(e: Throwable): IOException = e match {
      case err: IOException => err
      case err              => new IOException(err)
    }
  }

}
