import com.github.play2war.plugin._

name := """dobsite"""

version := "1.0-SNAPSHOT"

Play2WarPlugin.play2WarSettings

Play2WarKeys.servletVersion := "3.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala,SbtWeb)

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws
)

lazy val copy_node_modules = taskKey[Unit]("Copys the node_module to the test target dir")

copy_node_modules := {
  val node_modules = new File("node_modules")
  val target = new File("target/web/public/test/public/lib/")
  IO.copyDirectory(node_modules,target,true, true)
}

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "0.8.0",
  "io.strongtyped"			%% 	"active-slick"	% "0.2.2",
  "org.webjars" %% "webjars-play" % "2.3.0",
  "org.webjars" % "bootstrap" % "3.1.1-2",
  "org.seleniumhq.selenium" % "selenium-server" % "2.45.0" % "test",
  "org.seleniumhq.selenium" % "selenium-java" % "2.45.0" % "test",
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
  "org.scalatestplus" %% "play" % "1.2.0" % "test",
  "org.jsoup" % "jsoup" % "1.7.2",
  "jp.t2v" %% "play2-auth"      % "0.13.0",
  "jp.t2v" %% "play2-auth-test" % "0.13.0" % "test",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "commons-io" % "commons-io" % "2.4",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "3.6.1.201501031845-r",
  "org.webjars" % "font-awesome" % "4.3.0-1",
  "org.webjars" % "jquery" % "2.1.4",
  "org.webjars" % "jquery-ui" % "1.11.4",
  "mysql" % "mysql-connector-java" % "5.1.21",
  "net.sf.flexjson" % "flexjson" % "3.1",
  "org.webjars" % "should.js" % "5.0.0",
  "org.webjars" % "rjs" % "2.1.11-1-trireme" % "test"
)

Keys.fork in (Test) := true

pipelineStages := Seq(rjs, digest)

MochaKeys.requires += "./Setup"

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

addCommandAlias("js-test", ";web-assets:jseNpmNodeModules;copy_node_modules;mocha")

addCommandAlias("unit", "test-only test.unit.*Spec")

addCommandAlias("it", "test-only test.integration.*Spec")
