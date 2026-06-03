val scala3Version = "3.7.4"

lazy val root = project
  .in(file("."))
  .settings(
    name := "minimal-scala-project",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xfatal-warnings")
  )
