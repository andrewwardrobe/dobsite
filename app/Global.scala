/**
 * Created by andrew on 31/12/14.
 */

import com.daoostinboyeez.git.GitRepo
import play.api._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application Started")
    Logger.info("Git Branch: "+ GitRepo.getBranch)
  }

  override def onStop(app: Application) {
    Logger.info("Application stopped")
  }

}
