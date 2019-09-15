import Dependencies._

ThisBuild / organizationName := "Rivergnom"
ThisBuild / organization     := "com.rivergnom.common"
ThisBuild / scalaVersion     := "2.13.0"

lazy val root = (project in file("."))
  .settings(
    name := "common"
  )
  .dependsOn(util)

lazy val util = (project in file("util"))
  .settings(
    name                := "util",
    libraryDependencies ++= Seq(slf4j, logback, scalatest),
    libraryDependencies ++= Seq(akkaActor, akkaStream, akkaHttp)
  )
