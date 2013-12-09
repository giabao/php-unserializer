organization := "com.sandinh"

name := "php-unserializer"

version := "1.0.1"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-feature", "-Yinline-warnings"/*, "-optimise"*/)

javacOptions ++= Seq("-encoding", "UTF-8", "-source", "1.7", "-target", "1.7", "-Xlint:unchecked", "-Xlint:deprecation")

libraryDependencies += "org.scalatest"  %% "scalatest"  % "2.0.RC2"  % "test"

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/giabao/php-unserializer</url>
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:giabao/php-unserializer.git</url>
    <connection>scm:git:git@github.com:giabao/php-unserializer.git</connection>
  </scm>
  <developers>
    <developer>
      <id>giabao</id>
      <name>Gia Bảo</name>
      <email>giabao@sandinh.net</email>
      <organization>Sân Đình</organization>
      <organizationUrl>http://sandinh.com</organizationUrl>
    </developer>
    <developer>
      <id>lorecraft</id>
      <email>lorecraft@gmail.com</email>
    </developer>
    <developer>
      <id>forpdfsending</id>
      <email>forpdfsending@gmail.com</email>
    </developer>
    <developer>
      <id>ashawley</id>
      <name>Aaron S. Hawley</name>
      <email>aaron.s.hawley@gmail.com</email>
      <organization>NinthFloor.org</organization>
      <organizationUrl>http://users.ninthfloor.org/~ashawley/</organizationUrl>
    </developer>
  </developers>)
