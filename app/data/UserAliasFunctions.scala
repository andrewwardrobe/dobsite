package data

import java.util.UUID

import models.{UserAlias, UserAccount}
import play.api.db.slick.Session

/**
 *
 * Created by andrew on 11/07/15.
 *
 */
trait UserAliasFunctions {
  this: UserAliasSchema =>
  import play.api.db.slick.Config.driver.simple._


  def addAlias(user: UserAccount, newAlias: String)(implicit session: Session) = {
    val alias = new UserAlias(UUID.randomUUID.toString, user.id, newAlias)
    userAlias.insert(alias)
  }
}
