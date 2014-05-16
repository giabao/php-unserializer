organization := "com.sandinh"

name := "php-unserializer"

version := "1.0.4"

scalaVersion := "2.11.0"

crossScalaVersions := Seq("2.11.0", "2.10.4")

scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-feature", "-Xfuture", "-Yinline-warnings"/*, "-optimise"*/)

javacOptions ++= Seq("-encoding", "UTF-8", "-source", "1.7", "-target", "1.7", "-Xlint:unchecked", "-Xlint:deprecation")

libraryDependencies += "org.scalatest"  %% "scalatest"  % "2.1.6"  % "test"
