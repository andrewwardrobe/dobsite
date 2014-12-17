import com.github.play2war.plugin._

name := """dobsite"""

version := "1.0-SNAPSHOT"

Play2WarPlugin.play2WarSettings

Play2WarKeys.servletVersion := "3.1"

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
  "org.seleniumhq.selenium" % "selenium-server" % "2.44.0" % "test",
  "org.seleniumhq.selenium" % "selenium-java" % "2.44.0" % "test",
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
  "org.scalatestplus" %% "play" % "1.2.0" % "test",
  "org.jsoup" % "jsoup" % "1.7.2"
)

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")
