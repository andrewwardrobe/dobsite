/**
 * Created by andrew on 31/12/14.
 */

import java.io.File

import com.daoostinboyeez.git.GitRepo
import play.api._
import play.api.Play.current

object TestGlobal extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Test Application Started")
    Logger.info("Git Dir: "+ GitRepo.getRepoDir)
    Logger.info("Git Branch: "+ GitRepo.getBranch)
    Logger.info("Git Repo: "+Play.application.configuration.getString("git.repo.dir").get)
  }

  override def onStop(app: Application) {
    val repoDir = Play.application.configuration.getString("git.repo.dir").get
    val file = new File(repoDir.substring(0,repoDir.lastIndexOf("/.git")))
    Logger.info("Deleting file "+file.getAbsolutePath());
    file.delete()
    Logger.info("Application stopped")
  }



}
