package common

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, get, path}
import common.zioapp.ZioAkkaHttpServer
import zio.ZManaged

object ZHttpServer extends ZioAkkaHttpServer {

  override def binds: ZManaged[AkkaAppEnv, Throwable, Any] =
    bind("0.0.0.0", 8080, route1) *>
      bind("0.0.0.0", 8081, route2) *>
      bind("0.0.0.0", 8082, route3)

  private val route1 =
    path("hello") {
      get {
        complete(
          HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Say hello to zio-akka-http ${System.nanoTime()}</h1>")
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
        complete(throw new Exception("xxx!"))
      }
    }
  }

}
