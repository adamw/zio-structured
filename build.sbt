lazy val commonSettings = commonSmlBuildSettings ++ Seq(
  organization := "com.softwaremill.zio",
  scalaVersion := "2.13.2"
)

val scalaTest = "org.scalatest" %% "scalatest" % "3.1.0" % Test

lazy val rootProject = (project in file("."))
  .settings(commonSettings: _*)
  .settings(publishArtifact := false, name := "root")
  .aggregate(core)

val zioVersion = "1.0.0-RC20"

lazy val core: Project = (project in file("core"))
  .settings(commonSettings: _*)
  .settings(
    name := "core",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      scalaTest
    )
  )

