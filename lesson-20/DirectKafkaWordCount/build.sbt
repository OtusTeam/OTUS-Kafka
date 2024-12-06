name := "DirectKafkaWordCount"

version := "1.0"

scalaVersion := "2.12.12"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-Xfuture",
  "-encoding",
  "UTF-8",
  "-unchecked",
  "-language:postfixOps"
)

lazy val sparkVersion = "3.0.1"

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-streaming_2.12" % sparkVersion % "provided",
  "org.apache.spark" % "spark-streaming-kafka-0-10_2.12" % sparkVersion
)

assemblyMergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf")       => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*\\.sf$")   => MergeStrategy.discard
  case "reference.conf"                                 => MergeStrategy.concat
  case x: String if x.contains("UnusedStubClass.class") => MergeStrategy.first
  case _                                                => MergeStrategy.first
}
