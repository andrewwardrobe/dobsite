package test

/**
 * Created by andrew on 31/12/14.
 */

import java.io.File

import de.flapdoodle.embed.mongo.config.{Net, MongodConfigBuilder}

import de.flapdoodle.embed.mongo._
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import play.api.Play.current
import play.api._
import play.modules.reactivemongo.{ReactiveMongoHelper, ReactiveMongoPlugin}

object EmbedMongoGlobal extends GlobalSettings {

  var _mongodExe : MongodExecutable = _
  override def onStart(app: Application) {
    val mongoPort = Play.configuration.getInt("mongoembed.port").getOrElse(12345)
    Logger.info(s"Test Application Started - Mongo port $mongoPort")
    val starter = MongodStarter.getDefaultInstance()
    val config = new MongodConfigBuilder()
      .version(Version.Main.PRODUCTION)
      .net(new Net(mongoPort, Network.localhostIsIPv6()))
      .build()

    _mongodExe = starter.prepare(config)
    val _mongod = _mongodExe.start();


  }

  override def onStop(app: Application) {
    val repoDir = Play.application.configuration.getString("git.repo.dir").get
    val file = new File(repoDir.substring(0,repoDir.lastIndexOf("/.git")))
    //Logger.info("Deleting file "+file.getAbsolutePath());
    file.delete()
    reactivemongo.api.command
    //Logger.info("Application stopped")
    _mongodExe.stop()
  }



}
