name := "JsonParser"

version := "0.1"

scalaVersion := "2.12.6"
libraryDependencies ++=  Seq(
  "com.lihaoyi" %% "upickle" % "1.3.15",
  "org.scalatest" %% "scalatest" % "3.0.3" % Test
)