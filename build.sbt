name := """dobsite"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws
)

libraryDependencies += "com.typesafe.play" %% "play-slick" % "0.8.0"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.3.0",
  "org.webjars" % "bootstrap" % "3.1.1-2",
  "org.scalatestplus" %% "play" % "1.1.0" % "test",
  "org.seleniumhq.selenium" % "selenium-java" % "2.44.0" % "test",
  "org.jsoup" % "jsoup" % "1.7.2"
)
