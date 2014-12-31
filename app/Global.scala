/**
 * Created by andrew on 31/12/14.
 */
import play.api._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application Started")
  }

  override def onStop(app: Application) {
    Logger.info("Application stopped")
  }

}
