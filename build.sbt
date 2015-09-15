import play.sbt.PlayImport.PlayKeys._


name := """dobsite"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala,SbtWeb)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws
)

routesGenerator := InjectedRoutesGenerator

lazy val copy_node_modules = taskKey[Unit]("Copys the node_module to the test target dir")

copy_node_modules := {
  val node_modules = new File("node_modules")
  val target = new File("target/web/public/test/public/lib/")
  IO.copyDirectory(node_modules,target,true, true)
}

libraryDependencies += "org.webjars" % "webjars-locator-core" % "0.26"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.4.0-1",
  "org.webjars" % "bootstrap" % "3.3.5" exclude("org.webjars", "jquery"),
  "org.webjars" % "q" % "1.1.2",
  "org.seleniumhq.selenium" % "selenium-server" % "2.46.0",
  "org.seleniumhq.selenium" % "selenium-java" % "2.46.0",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "org.scalatestplus" %% "play" % "1.4.0-M4" % "test",
  "org.jsoup" % "jsoup" % "1.7.2",
  "jp.t2v" %% "play2-auth"      % "0.14.1",
  "jp.t2v" %% "play2-auth-test" % "0.14.1" % "test",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "commons-io" % "commons-io" % "2.4",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "3.6.1.201501031845-r",
  "org.webjars" % "font-awesome" % "4.3.0-1",
  "org.webjars" % "jquery" % "2.1.4",
  "org.webjars" % "jquery-ui" % "1.11.4",
  "mysql" % "mysql-connector-java" % "5.1.35",
  "net.sf.flexjson" % "flexjson" % "3.1",
  "org.webjars" % "should.js" % "5.0.0",
  "org.webjars" % "rjs" % "2.1.11-1-trireme" % "test",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.2.play24",
  "com.github.simplyscala" %% "scalatest-embedmongo" % "0.2.2" % "test"
)

Keys.fork in Test := true

pipelineStages := Seq(rjs, digest)

MochaKeys.requires += "./Setup"

RjsKeys.paths += ("jsRoutes" -> ("/jsroutes" -> "empty:"))

JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

addCommandAlias("js-test", ";web-assets:jseNpmNodeModules;copy_node_modules;mocha")

addCommandAlias("unit", "test-only test.unit.*Spec")

addCommandAlias("it", "test-only test.integration.*Spec")
