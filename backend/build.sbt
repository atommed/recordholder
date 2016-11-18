name := "RecordHolder"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  jdbc,
  "com.typesafe.play" %% "anorm" % "2.5.2"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)
