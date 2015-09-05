package test.helpers

import play.api.inject.guice.GuiceApplicationBuilder
import test.{EmbedMongoGlobal, TestConfig}

import scala.collection.JavaConversions._

/**
 *
 * Created by andrew on 03/09/15.
 *
 */
trait ReactiveMongoApp {

  import scala.collection.JavaConversions.iterableAsScalaIterable

  def buildApp = {
    val env = play.api.Environment.simple(mode = play.api.Mode.Test)
    val config = play.api.Configuration.load(env)
    val modules = config.getStringList("play.modules.enabled").fold(
      List.empty[String])(l => iterableAsScalaIterable(l).toList)

   new GuiceApplicationBuilder()
      .configure(TestConfig.withTempGitRepo ++ TestConfig.withEmbbededMongo)
      .configure("play.modules.enabled" -> (modules :+ "play.modules.reactivemongo.ReactiveMongoModule"))
      .global(EmbedMongoGlobal)
      .build
  }
}
