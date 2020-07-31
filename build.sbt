import Dependencies._

ThisBuild / organizationName := "Rivergnom"
ThisBuild / organization     := "com.rivergnom.common"
ThisBuild / scalaVersion     := "2.13.0"
scalacOptions in ThisBuild   ++= Seq("-unchecked", "-deprecation")
enablePlugins(PackPlugin)

lazy val common = (project in file("."))
  .settings(
    name := "common"
  )
  .dependsOn(core, util, proto)

lazy val dictionary = (project in file("dictionary"))
  .settings(
    name := "dictionary"
  )
  .dependsOn(core, util)

lazy val scheduler = (project in file("scheduler"))
  .settings(
    name := "scheduler"
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
    libraryDependencies ++= Seq(playJson, playJsonExt),
    libraryDependencies ++= Seq(akkaSlf4j, akkaActor, akkaStream, akkaHttp),
    libraryDependencies ++= Seq(zio, shapeless, cats),
    libraryDependencies += im4java
  )

lazy val proto = (project in file("proto"))
  .settings(
    name := "proto",
    PB.targets in Compile := Seq(
      PB.gens.java -> (sourceManaged in Compile).value,
//      scalapb.gen(javaConversions = true) -> (sourceManaged in Compile).value
    )
  )
