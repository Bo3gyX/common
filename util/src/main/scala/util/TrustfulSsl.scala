package util

import java.security.cert.X509Certificate

import javax.net.ssl.{KeyManager, SSLContext, X509TrustManager}

object TrustfulSsl {

  object NoCheckX509TrustManager extends X509TrustManager {
    override def checkClientTrusted(chain: Array[X509Certificate], authType: String): Unit = ()

    override def checkServerTrusted(chain: Array[X509Certificate], authType: String): Unit = ()

    override def getAcceptedIssuers: Array[X509Certificate] = Array[X509Certificate]()
  }

  def context: SSLContext = {
    val context = SSLContext.getInstance("TLS")
    context.init(Array[KeyManager](), Array(NoCheckX509TrustManager), null)
    context
  }

}
