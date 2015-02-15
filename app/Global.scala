/**
 * Created by andrew on 31/12/14.
 */

import com.daoostinboyeez.git.GitRepo
import play.api._
import play.api.Play.current

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application Started")
    Logger.info("Git Dir: "+ GitRepo.getRepoDir)
    Logger.info("Git Branch: "+ GitRepo.getBranch)
    Logger.info("Git Repo: "+Play.application.configuration.getString("git.repo.dir").get)
  }

  override def onStop(app: Application) {
    Logger.info("Application stopped")
  }

}
