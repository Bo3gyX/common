package common

object Curl {

  //курл запрос с проксей
  //тоже но с сертом
  val proxy = "--proxy proxy-ext.test.vertis.yandex.net:3128"
//  val host = "https://api.raiffeisen.ru/private/openapi-010-ssl/loan-leads-retail/app/"
  val host = "https://api.raiffeisen.ru/dlpi-partner/v1/applications/OAPI20201207PLL002164654111060/partner/states/current"

//  val method = "POST"
  val method = "GET"

  val contentType = "Content-Type: application/json"
  val userAgent = "User-Agent: Yandex-Vertis client"

  val cert = "--cert /Users/ridrisov/work/raif-prod-cert/file.crt.pem"
  val key = "--key /Users/ridrisov/work/raif-prod-cert/file.key.pem"

  val certType = "--cert-type P12"
//  val certPath = "--cert /Users/ridrisov/work/raif-prod-cert/raiffeisen.p12"
  val certPath = "--cert /Users/ridrisov/work/raif-prod-cert/keyStore.p12:123"

//  val curl = s"curl -vvv -X $method -H $contentType -H $userAgent $host $proxy $cert $key"
  val curl = s"curl -v -X $method -H '$contentType' -H '$userAgent' $host $proxy $certType $certPath"

  val r ="{\"content\":\"Hello, World!\",\"tts\":false,\"embeds\":[{\"title\":\"Hello, Embed!\",\"description\":\"This is an embedded message.\"}]}"
  println(curl)

}
