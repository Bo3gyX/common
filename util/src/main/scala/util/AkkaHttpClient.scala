package util

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, HttpResponse, StatusCode}
import akka.http.scaladsl.{Http, HttpsConnectionContext}
import akka.stream.Materializer

import scala.concurrent.Future
import scala.concurrent.duration._

trait AkkaHttpClient extends Logger {

  implicit val actorSystem: ActorSystem
  implicit val materializer: Materializer

  import actorSystem.dispatcher

  val timeout: FiniteDuration = 3.seconds
  val disableSSL: Boolean = false

  private val hctx = {
    val ctx = Http().defaultClientHttpsContext
    if (disableSSL) {
      new HttpsConnectionContext(
        TrustfulSsl.context,
        ctx.sslConfig,
        ctx.enabledCipherSuites,
        ctx.enabledProtocols,
        ctx.clientAuth,
        ctx.sslParameters
      )
    } else {
      ctx
    }
  }

  def doRequest[T](req: HttpRequest)(pf: PartialFunction[HttpResponse, Future[T]]): Future[T] = {
    val st = System.currentTimeMillis()

    Http().singleRequest(req, hctx).flatMap { response =>

      val result = {
        if (pf.isDefinedAt(response)) {
          pf(response)
        } else {
          response.discardEntityBytes()
          val err = s"Unexpected response status. ${response.status}"
          log.error(err)
          Future.failed(new Exception(err))
        }
      }

      val et = System.currentTimeMillis()
      val tt = et - st
      val td = tt / 1e3d
      //      log.info(s"st: $st, et: $et, tt: $tt")
      log.info(s"request ${td}s. ${req.uri}")
      result
    }
  }

  def withStrictCode[T](codes: StatusCode*)(f: HttpEntity.Strict => T): PartialFunction[HttpResponse, Future[T]] = {
    case HttpResponse(code, _, entity, _) if codes.contains(code) =>
      entity.toStrict(timeout).map(f)
  }
}