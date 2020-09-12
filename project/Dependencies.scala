import sbt._

object Dependencies {
  lazy val slf4j       = "org.slf4j" % "slf4j-api" % "1.7.12"
  lazy val logback     = "ch.qos.logback" % "logback-classic" % "1.2.3"
  lazy val logging     = Seq(slf4j, logback)
  lazy val playJson    = "com.typesafe.play" %% "play-json" % "2.8.1"
  lazy val playJsonExt = "ai.x" %% "play-json-extensions" % "0.42.0"

  lazy val scalatest = "org.scalatest" %% "scalatest" % "3.0.8"

  lazy val akkaActor  = "com.typesafe.akka" %% "akka-actor"  % "2.6.3"
  lazy val akkaSlf4j  = "com.typesafe.akka" %% "akka-slf4j"  % "2.6.3"
  lazy val akkaStream = "com.typesafe.akka" %% "akka-stream" % "2.6.3"
  lazy val akkaHttp   = "com.typesafe.akka" %% "akka-http"   % "10.1.9"

  //todo то что хочется понять и научится юзать
  lazy val shapeless = "com.chuusai"   %% "shapeless" % "2.3.3"
  lazy val cats      = "org.typelevel" %% "cats-core" % "2.1.0"
  lazy val zio       = "dev.zio"       %% "zio"       % "1.0.0-RC17"

  //image support
  lazy val ffmpeg = "net.bramp.ffmpeg" % "ffmpeg"  % "0.6.2"
  lazy val im4java = "org.im4java"     % "im4java" % "1.4.0"
  lazy val images = Seq(ffmpeg, im4java)

  lazy val protoUtils = "com.google.protobuf" % "protobuf-java-util" % "3.13.0"
}
