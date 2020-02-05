lazy val commonSettings = commonSmlBuildSettings ++ ossPublishSettings ++ Seq(
  organization := "com.softwaremill.zio",
  scalaVersion := "2.13.1"
)

val scalaTest = "org.scalatest" %% "scalatest" % "3.1.0" % Test

lazy val rootProject = (project in file("."))
  .settings(commonSettings: _*)
  .settings(publishArtifact := false, name := "root")
  .aggregate(core)

val zioVersion = "1.0.0-RC17+325-13ddb36d-SNAPSHOT"

lazy val core: Project = (project in file("core"))
  .settings(commonSettings: _*)
  .settings(
    name := "core",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      scalaTest
    )
  )

