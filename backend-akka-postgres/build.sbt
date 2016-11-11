name := "backend-akka-postgres"

version := "1.0.0"

scalaVersion := "2.11.8"

scalacOptions ++= Seq(
  "-feature",
  "-language:reflectiveCalls",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-language:higherKinds")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.11",
  "org.sangria-graphql" %% "sangria" % "1.0.0-RC3",
  "org.sangria-graphql" %% "sangria-circe" % "0.6.0",
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.h2database" % "h2" % "1.4.193")
