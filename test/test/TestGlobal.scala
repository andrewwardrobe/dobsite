package test

/**
 * Created by andrew on 31/12/14.
 */

import java.io.File

import com.github.simplyscala.{MongoEmbedDatabase, MongodProps}
import play.api.Play.current
import play.api._

object TestGlobal extends GlobalSettings{


  override def onStart(app: Application) {

  }

  override def onStop(app: Application) {
    val repoDir = Play.application.configuration.getString("git.repo.dir").get
    val file = new File(repoDir.substring(0,repoDir.lastIndexOf("/.git")))
    //Logger.info("Deleting file "+file.getAbsolutePath());
    file.delete()
    //Logger.info("Application stopped")

  }



}
