package common.zioapp

import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import sttp.client3.SttpBackendOptions.{Proxy, ProxyType}
import sttp.client3._
import sttp.client3.httpclient.zio.SttpClient.Service
import sttp.client3.httpclient.zio.{HttpClientZioBackend, SttpClient}
import sttp.model.Header
import util.Logger
import zio.{ZLayer, _}

import java.io.FileInputStream
import java.net.http.HttpClient
import java.net.{Authenticator, PasswordAuthentication}
import java.security.{KeyStore, SecureRandom}
import java.time.Duration
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}

object MyHttpClient extends App with Logger {

  class ProxyAuthenticator(auth: SttpBackendOptions.ProxyAuth) extends Authenticator {

    override def getPasswordAuthentication: PasswordAuthentication = {
      new PasswordAuthentication(auth.username, auth.password.toCharArray)
    }
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    process.provideLayer(sttpClientLayer).exitCode
  }

  val sttpClientLayer: ZLayer[Any, Throwable, SttpClient] = {

    val proxy: Option[Proxy] = Some(
      Proxy(
        host = "proxy-ext.test.vertis.yandex.net",
        port = 3128,
        proxyType = ProxyType.Http
      )
    )

    val certPath     = "/Users/ridrisov/work/raif-prod-cert/raiffeisen.p12"
    val certPassword = "VAgUBsvidYCHSSxJnqmpL6n9auNbNRXe0wZtCd422fA=".toCharArray

    val sslContext: Option[SSLContext] = {

      val trustManagerFactorySecure = false

      val ta = TrustManagerFactory.getDefaultAlgorithm
      println(s"TrustManagerFactory.getDefaultAlgorithm: $ta")

      val ka = KeyManagerFactory.getDefaultAlgorithm
      println(s"KeyManagerFactory.getDefaultAlgorithm: $ka")

      val keyStore = {
        val ks = KeyStore.getInstance("PKCS12")
        ks.load(new FileInputStream(certPath), certPassword)
        ks
      }

      val trustManagerFactory = {
        if (trustManagerFactorySecure) TrustManagerFactory.getInstance(ta)
        else InsecureTrustManagerFactory.INSTANCE
      }

      val keyManagerFactory = {
        val kmf = KeyManagerFactory.getInstance(ka)
        kmf.init(keyStore, certPassword)
        kmf
      }

      val context = {
        val ctx = SSLContext.getInstance("TLS")
        ctx.init(keyManagerFactory.getKeyManagers, trustManagerFactory.getTrustManagers, new SecureRandom())
        ctx
      }
      Some(context)
    }

    val defaultClient: HttpClient = {

      var clientBuilder = HttpClient
        .newBuilder()
        .followRedirects(HttpClient.Redirect.NEVER)
        .connectTimeout(Duration.ofSeconds(5))

      clientBuilder = sslContext match {
        case None => clientBuilder
        case Some(sslContext) => clientBuilder.sslContext(sslContext)
      }

      clientBuilder = proxy match {
        case None => clientBuilder
        case Some(p @ Proxy(_, _, _, _, Some(auth))) =>
          clientBuilder.proxy(p.asJavaProxySelector).authenticator(new ProxyAuthenticator(auth))
        case Some(p) => clientBuilder.proxy(p.asJavaProxySelector)
      }

      println(clientBuilder)

      clientBuilder.build()
    }

    HttpClientZioBackend.layerUsingClient(defaultClient)
  }

  val process: ZIO[Has[Service], Throwable, Unit] = {
    for {
      client   <- ZIO.service[SttpClient.Service]
      response <- send(RaiffeisenBankClient.request)(client)
    } yield {
      println(response.code)
      println(response.body)
    }
  }

  def send(uri: String)(implicit client: SttpClient.Service): Task[Response[String]] = {
    send(quickRequest.get(uri"$uri"))
  }

  def send(request: Request[String, Any])(implicit client: SttpClient.Service): Task[Response[String]] = {
    println(request.toCurl)
    request.send(client)
  }

  object RaiffeisenBankClient {

    val uri =
      uri"https://api.raiffeisen.ru/dlpi-partner/v1/applications/OAPI20201207PLL002164654111060/partner/states/current"

    val request: Request[String, Any] = quickRequest
      .header(Header.accept("application/json"))
      .header(Header.userAgent("Yandex-Vertis client"))
      .get(uri)
  }
}
