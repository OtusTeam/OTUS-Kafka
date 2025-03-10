ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

lazy val root = (project in file("."))
  .settings(
    name := "lesson-17"
  )

val AkkaVersion = "2.10.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.5.11",
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,

  "com.typesafe.akka" %% "akka-stream-kafka" % "7.0.1",

  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion,
  "org.scalatest" %% "scalatest" % "3.2.19" % Test
)
