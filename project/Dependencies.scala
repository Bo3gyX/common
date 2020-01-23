import sbt._

object Dependencies {
  lazy val slf4j   = "org.slf4j" % "slf4j-api" % "1.7.12"
  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
  lazy val logging = Seq(slf4j, logback)

  lazy val scalatest = "org.scalatest" %% "scalatest" % "3.0.8"

  lazy val akkaActor  = "com.typesafe.akka"      %% "akka-actor"  % "2.5.25"
  lazy val akkaSlf4j  = "com.typesafe.akka"      %% "akka-slf4j"  % "2.5.25"
  lazy val akkaStream = "com.typesafe.akka"      %% "akka-stream" % "2.5.25"
  lazy val akkaHttp   = "com.typesafe.akka"      %% "akka-http"   % "10.1.9"
  lazy val zio        = "dev.zio"                %% "zio"         % "1.0.0-RC17"
  lazy val shapeless  = "com.chuusai"            %% "shapeless"   % "2.3.3"
  lazy val playJson   = "com.typesafe.play"      %% "play-json"   % "2.8.1"
}
