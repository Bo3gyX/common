package common.zioapp

import util.zio.ZioRunner
import util.zio.ZioRunner.AppEnv
import zio.macros.accessible
import zio.{Has, Task, ULayer, URLayer, ZIO, ZLayer}

@accessible
object Echo {

  trait Service {
    def repeated(in: String, n: Int): Task[String]
    def echo(in: String): Task[String]
  }

  val live: ULayer[Has[Echo.Service]] = ZLayer.succeed(new EchoImpl)
}

class EchoImpl extends Echo.Service {

  override def repeated(in: String, n: Int): Task[String] =
    Task.succeed(Seq.fill(n)(in).mkString(","))

  override def echo(in: String): Task[String] =
    Task.succeed(s"repl: $in")
}

@accessible
object MainProgram {

  trait Service {
    def testEcho(in: String): Task[String]
  }

  val live: URLayer[Has[Echo.Service], Has[MainProgram.Service]] = (for {
    echo <- ZIO.service[Echo.Service]
  } yield new MainProgramImpl(echo)).toLayer
}

class MainProgramImpl(echo: Echo.Service) extends MainProgram.Service {

  override def testEcho(in: String): Task[String] = {
    for {
      r1 <- echo.echo(in)
      r2 <- echo.echo(in)
      r3 <- echo.repeated(in, 3)
    } yield s"$r1 -> $r2|$r3"
  }
}

object ExampleProgramApp extends ZioRunner {

  val layer: ULayer[Has[MainProgram.Service]] =
    Echo.live >>> MainProgram.live

  override def start: ZIO[AppEnv, Any, Any] = {
    val t = MainProgram.testEcho("test")
    val r = t.provideCustomLayer(layer)
    r
  }
}
