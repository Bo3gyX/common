package common.zioapp
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import zio._
import zio.logging.{log, Logging}

import scala.concurrent.duration.{FiniteDuration, SECONDS}

trait ZioAkkaHttpServer extends ZioRunner {

  type AkkaAppEnv = ZEnv with Logging with Has[ActorSystem]

  def binds: ZManaged[AkkaAppEnv, Throwable, Any]

  override def start: ZIO[AppEnv, Any, Any] = {
    val appLayer = ZLayer.requires[AppEnv] ++ actorSystemLayer("ZIO-ActorSystem")
    binds.useForever.provideLayer(appLayer)
  }

  protected def bind(
      host: String,
      port: Int,
      route: Route): ZManaged[Logging with Has[ActorSystem], Throwable, Http.ServerBinding] = {
    bindManaged(host, port, route)
  }

  private def actorSystemLayer(name: String) = {
    val actorSystem = ActorSystem(name)

    def acquire = {
      for {
        _  <- log.info(s"initializing ActorSystem $name")
        as <- UIO(actorSystem)
        _  <- log.info(s"initialized ActorSystem $name")
      } yield as
    }

    def release(actorSystem: ActorSystem) = {
      for {
        _   <- log.info(s"stopping ActorSystem $name")
        res <- ZIO.fromFuture(_ => actorSystem.terminate()).orDie
        _   <- log.info(s"stopped ActorSystem $name")
      } yield res
    }

    ZManaged.make(acquire)(release).toLayer
  }

  private def bindManaged(host: String, port: Int, route: Route) = {
    def acquire(implicit system: ActorSystem) =
      for {
        _    <- log.info(s"Http server binding $host:$port")
        bind <- Task.fromFuture(_ => Http().newServerAt(host, port).bind(route))
        _    <- log.info(s"Http server binded $host:$port")
      } yield bind

    def release(bind: Http.ServerBinding)(implicit system: ActorSystem) = {
      for {
        _          <- log.info(s"unbinding $host:$port")
        terminated <- Task.fromFuture(_ => bind.terminate(FiniteDuration(5, SECONDS))).orDie
        _          <- log.info(s"unbinded $host:$port")
      } yield terminated
    }

    for {
      as <- ZManaged.service[ActorSystem]
      m  <- ZManaged.make(acquire(as))(release(_)(as))
    } yield m

  }
}
