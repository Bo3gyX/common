package common

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import common.zioapp.AkkaActorSystem
import common.zioapp.AkkaActorSystem.AkkaActorSystem
import util.zio.ZioRunner
import zio._
import zio.logging.{log, Logging}

import scala.concurrent.duration.{FiniteDuration, SECONDS}

object ZHttpServer extends ZioRunner {

  private val route1 =
    path("hello") {
      (get & parameter("p".?)) { p =>
        complete(
          if (p.contains("xxx")) throw new Exception("AAA!")
          else {
            HttpEntity(
              ContentTypes.`text/html(UTF-8)`,
              s"<h1>Say hello to zio-akka-http ${System.nanoTime()}</h1><p>$p</p>"
            )
          }
        )
      }
    }

  private val route2 = {
    path("ping") {
      get {
        complete(s"pong")
      }
    }
  }

  private val route3 = {
    path("test") {
      get {
        complete("Test")
      }
    }
  }

  override def start: ZIO[ZHttpServer.AppEnv, Any, Any] = {
    val actorSystemLayer = ZLayer.requires[Logging] ++ AkkaActorSystem.Live("zio-http-service")

    val eff = for {
      _ <- binding(8080, route1)
      _ <- binding(8081, route2)
      _ <- binding(8082, route3)
    } yield ()

    eff.useForever.provideLayer(actorSystemLayer)
  }

  def binding(port: Int, route: Route): ZManaged[Logging with AkkaActorSystem, Throwable, Http.ServerBinding] =
    binding("0.0.0.0", port, route)

  def binding(
      host: String,
      port: Int,
      route: Route): ZManaged[Logging with AkkaActorSystem, Throwable, Http.ServerBinding] = {

    def acquire(route: Route)(implicit actorSystem: ActorSystem) =
      for {
        _       <- log.info(s"Http server binding $host:$port")
        binding <- Task.fromFuture(_ => Http().newServerAt(host, port).bind(route))
        _       <- log.info(s"Http server binded $host:$port")
      } yield binding

    def release(binding: Http.ServerBinding)(implicit system: ActorSystem) = {
      for {
        _          <- log.info(s"unbinding $host:$port")
        terminated <- Task.fromFuture(_ => binding.terminate(FiniteDuration(5, SECONDS))).orDie
        _          <- log.info(s"unbinded $host:$port")
      } yield terminated
    }

    for {
      service <- ZManaged.service[AkkaActorSystem.Service]
      managed <- ZManaged.make(acquire(route)(service.actorSystem))(release(_)(service.actorSystem))
    } yield managed
  }

}
