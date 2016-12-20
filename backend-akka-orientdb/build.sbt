import com.github.retronym.SbtOneJar._

name := "backend-akka-orientdb"

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
  "org.sangria-graphql" %% "sangria" % "1.0.0-RC3",
  "org.sangria-graphql" %% "sangria-circe" % "0.6.0",
  "io.circe" %% "circe-parser" % "0.6.0",
  "io.circe" %% "circe-optics" % "0.6.0",
  "com.michaelpollmeier" %% "gremlin-scala" % "3.2.3.1",
  "com.michaelpollmeier" % "orientdb-gremlin" % "3.2.3.0")
  
def oneJarMappings(path: String) = (file(path) ** "*.*").get.map { f =>
  f -> f.getPath.replaceFirst(s"$path/", "")
}

mappings in oneJar ++= oneJarMappings("src/main/resources")
