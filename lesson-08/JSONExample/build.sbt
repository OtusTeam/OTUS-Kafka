ThisBuild / organization := "ru.example"
ThisBuild / version := "1.0"
ThisBuild / scalaVersion := "2.13.8"

lazy val configVersion  = "1.4.2"
lazy val kafkaVersion   = "3.2.3"
lazy val csvVersion     = "1.9.0"
lazy val circeVersion   = "0.14.3"
lazy val logbackVersion = "1.4.1"

ThisBuild / libraryDependencies ++= Seq(
  "com.typesafe"       % "config"          % configVersion,
  "org.apache.kafka"   % "kafka-clients"   % kafkaVersion,
  "org.apache.commons" % "commons-csv"     % csvVersion,
  "io.circe"          %% "circe-core"      % circeVersion,
  "io.circe"          %% "circe-generic"   % circeVersion,
  "io.circe"          %% "circe-parser"    % circeVersion,
  "ch.qos.logback"     % "logback-classic" % logbackVersion
)

lazy val root = (project in file("."))
  .settings(name := "Producer")
  .settings(assembly / mainClass := Some("ru.example.kafka.producer.Producer"))
  .settings(assembly / assemblyJarName := "producer.jar")
  .settings(assembly / assemblyMergeStrategy := {
    case m if m.toLowerCase.endsWith("manifest.mf")       => MergeStrategy.discard
    case m if m.toLowerCase.matches("meta-inf.*\\.sf$")   => MergeStrategy.discard
    case "module-info.class"                              => MergeStrategy.first
    case "version.conf"                                   => MergeStrategy.discard
    case "reference.conf"                                 => MergeStrategy.concat
    case x: String if x.contains("UnusedStubClass.class") => MergeStrategy.first
    case _                                                => MergeStrategy.first
  })
  