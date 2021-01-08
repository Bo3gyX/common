import sbt._

object Dependencies {
  lazy val slf4j    = "org.slf4j" % "slf4j-api" % "1.7.12"
  lazy val logback  = "ch.qos.logback" % "logback-classic" % "1.2.3"
  lazy val logstash = "net.logstash.logback" % "logstash-logback-encoder" % "6.5"
  lazy val logging  = Seq(slf4j, logback, logstash)

  lazy val playJson    = "com.typesafe.play" %% "play-json"            % "2.8.1"
  lazy val playJsonExt = "ai.x"              %% "play-json-extensions" % "0.42.0"

  lazy val circe = {
    val version = "0.12.3"
    Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % version)
  }
  lazy val circeGenericExtras = "io.circe" %% "circe-generic-extras" % "0.12.2"

  lazy val scalatest = "org.scalatest" %% "scalatest" % "3.0.8"

  lazy val akkaActor  = "com.typesafe.akka" %% "akka-actor"  % "2.6.3"
  lazy val akkaSlf4j  = "com.typesafe.akka" %% "akka-slf4j"  % "2.6.3"
  lazy val akkaStream = "com.typesafe.akka" %% "akka-stream" % "2.6.3"
  lazy val akkaHttp   = "com.typesafe.akka" %% "akka-http"   % "10.2.2"

  //todo то что хочется понять и научится юзать
  lazy val shapeless       = "com.chuusai"             %% "shapeless"         % "2.3.3"
  lazy val cats            = "org.typelevel"           %% "cats-core"         % "2.1.0"
  lazy val zio             = "dev.zio"                 %% "zio"               % "1.0.3"
  lazy val zioLogging      = "dev.zio"                 %% "zio-logging"       % "0.5.4"
  lazy val zioLoggingSlf4j = "dev.zio"                 %% "zio-logging-slf4j" % "0.5.4"
  lazy val tagging         = "com.softwaremill.common" %% "tagging"           % "2.2.1"

  //image support
  lazy val ffmpeg  = "net.bramp.ffmpeg" % "ffmpeg" % "0.6.2"
  lazy val im4java = "org.im4java" % "im4java" % "1.4.0"
  lazy val images  = Seq(ffmpeg, im4java)

  lazy val protoUtils = "com.google.protobuf"    % "protobuf-java-util" % "3.13.0"
  lazy val xml        = "org.scala-lang.modules" %% "scala-xml"         % "1.2.0"

  lazy val phoneNumber = "com.googlecode.libphonenumber" % "libphonenumber" % "7.0.3"
  lazy val enumeratum  = "com.beachape"                  %% "enumeratum"    % "1.6.1"

  lazy val sttp3BackendZio = "com.softwaremill.sttp.client3" %% "httpclient-backend-zio" % "3.0.0-RC11"
  lazy val sttpModel       = "com.softwaremill.sttp.model"   %% "core"                   % "1.2.0-RC8"
  lazy val asyncHttpClient = "org.asynchttpclient"           % "async-http-client"       % "2.12.1"

  lazy val monocle = Seq(
    "com.github.julien-truffaut" %% "monocle-core"  % "2.0.3",
    "com.github.julien-truffaut" %% "monocle-macro" % "2.0.3"
  )
}
