import Dependencies._

ThisBuild / organizationName := "Rivergnom"
ThisBuild / organization     := "com.rivergnom.common"
ThisBuild / scalaVersion     := "2.13.5"
scalacOptions in ThisBuild   ++= Seq("-unchecked", "-deprecation", "-Ymacro-annotations")
enablePlugins(PackPlugin)

lazy val scheduler = (project in file("scheduler"))
  .settings(
    name := "scheduler"
  )
  .dependsOn(core, util)

lazy val webSocket = (project in file("websocket"))
  .settings(
    name := "websocket"
  )
  .dependsOn(core, util)

lazy val common = (project in file("."))
  .settings(
    name := "common",
    libraryDependencies ++= zioTest,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
  .dependsOn(core, util, proto)

lazy val core = (project in file("core"))
  .settings(
    name := "core"
  )

lazy val util = (project in file("util"))
  .settings(
    name                := "util",
    libraryDependencies ++= logging,
    libraryDependencies += typesafeConfig,
    libraryDependencies ++= Seq(playJson, playJsonExt),
    libraryDependencies ++= Seq(akkaSlf4j, akkaActor, akkaStream, akkaHttp),
    libraryDependencies ++= Seq(zio, zioLogging, zioLoggingSlf4j, zioMacros),
    libraryDependencies ++= Seq(shapeless, cats, tagging),
    libraryDependencies ++= Seq(xml),
    libraryDependencies ++= circe ++ Seq(circeGenericExtras, circeDerivation),
    libraryDependencies += phoneNumber,
    libraryDependencies += enumeratum,
    libraryDependencies ++= Seq(sttp3BackendZio, sttpModel, asyncHttpClient, sttp3AsyncBackendZio),
    libraryDependencies ++= monocle,
    libraryDependencies ++= zioTest,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

lazy val proto = (project in file("proto"))
  .settings(
    name                := "proto",
    libraryDependencies += protoUtils,
    PB.targets in Compile := Seq(
      PB.gens.java -> (sourceManaged in Compile).value,
      scalapb.gen(javaConversions = true) -> (sourceManaged in Compile).value
    )
  )
