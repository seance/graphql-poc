import com.github.retronym.SbtOneJar._

name := "backend-akka-postgres"

version := "1.0.0"

scalaVersion := "2.11.8"

oneJarSettings

scalacOptions ++= Seq(
  "-feature",
  "-language:reflectiveCalls",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-language:higherKinds")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.0-RC2",
  "de.heikoseeberger" %% "akka-http-circe" % "1.11.0-M1",
  "org.sangria-graphql" %% "sangria" % "1.0.0-RC5",
  "org.sangria-graphql" %% "sangria-circe" % "0.6.0",
  "io.circe" %% "circe-parser" % "0.6.0",
  "io.circe" %% "circe-optics" % "0.6.0",
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1",
  "org.postgresql" % "postgresql" % "9.4.1211",
  "commons-lang" % "commons-lang" % "2.6",
  "ch.qos.logback" % "logback-classic" % "1.1.8",
  "org.slf4j" % "slf4j-api" % "1.7.22")
  
def oneJarMappings(path: String) = (file(path) ** "*.*").get.map { f =>
  f -> f.getPath.replaceFirst(s"$path/", "")
}

mappings in oneJar ++= oneJarMappings("src/main/resources")
