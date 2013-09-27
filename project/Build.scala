import sbt._
import sbt.Keys._

object Build extends sbt.Build {
  val baseSettings = Project.defaultSettings ++ Seq(
    scalaVersion := "2.10.3",
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-feature", "-Yinline-warnings"/*, "-optimise"*/),
    javacOptions  ++= Seq("-encoding", "UTF-8", "-source", "1.7", "-target", "1.7", "-Xlint:unchecked", "-Xlint:deprecation")
  )

  lazy val common = Project(
    id = "php-unserializer",
    base = file("."),
    settings = baseSettings ++ Seq(
      name := "php-unserializer",
      organization := "sandinh",
      version := "1.0",
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest"      % "2.0.M8"  % "test"
      )
    )
  )
}

