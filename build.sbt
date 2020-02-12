import Dependencies._

ThisBuild / organizationName := "Rivergnom"
ThisBuild / organization     := "com.rivergnom.common"
ThisBuild / scalaVersion     := "2.13.0"
enablePlugins(PackPlugin)

lazy val common = (project in file("."))
  .settings(
    name := "common"
  )
  .dependsOn(core, util)

lazy val dictionary = (project in file("dictionary"))
  .settings(
    name := "dictionary"
  )
  .dependsOn(core, util)

lazy val core = (project in file("core"))
  .settings(
    name := "core"
  )

lazy val util = (project in file("util"))
  .settings(
    name                := "util",
    libraryDependencies ++= logging,
    libraryDependencies ++= Seq(akkaSlf4j, akkaActor, akkaStream, akkaHttp, zio, shapeless),
    libraryDependencies += playJson
  )
