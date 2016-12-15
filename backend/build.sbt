name := "RecordHolder"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  filters,
  evolutions,
  jdbc,
  "org.postgresql" % "postgresql" % "9.4.1212",
  "com.typesafe.play" %% "anorm" % "2.5.0"
)

scalacOptions ++= Seq("-feature")

lazy val root = (project in file(".")).enablePlugins(PlayScala)
