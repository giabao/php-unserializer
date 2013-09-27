import sbt._
import sbt.Keys._

object Build extends sbt.Build {
  val baseSettings = Project.defaultSettings ++ Seq(
    scalaVersion := "2.10.2",
    scalacOptions ++= Seq("-feature", "-deprecation")
  )

  lazy val common = Project(
    id = "serialized-php-parser",
    base = file("."),
    settings = baseSettings ++ Seq(
      name := "serialized-php-parser",
      organization := "name.khartn",
      version := "1.0",
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest"      % "2.0.M8"  % "test",
        "com.novocode"  % "junit-interface" % "0.10"    % "test" //for running junit in sbt
      )
    )
  )
}

