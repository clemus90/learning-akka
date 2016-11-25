name := """hello-akka"""

version := "1.0"

scalaVersion := "2.11.6"

lazy val akkaVersion = "2.4.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion
)

