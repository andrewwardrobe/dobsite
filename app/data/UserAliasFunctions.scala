package data

import java.util.UUID

import com.daoostinboyeez.site.exceptions.AliasLimitReachedException
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

  def aliasAvailable(alias: String)(implicit session: Session) = {
    true
  }

  def aliasCount(user: UserAccount)(implicit session: Session) = {
    userAlias.filter(_.userId === user._id).list.length
  }

  def addAlias(user: UserAccount, newAlias: String)(implicit session: Session) = {
    val alias = new UserAlias(UUID.randomUUID.toString, user._id, newAlias)
    if (aliasAvailable(newAlias)) {
      if (aliasCount(user) >= user.getAliasLimit) {
        throw new AliasLimitReachedException("Limit Reached")
      }
      else {
        userAlias.insert(alias)
      }
      true
    } else {
      false
    }
  }

  def removeAllAliases(implicit session: Session) = {
    userAlias.delete
  }
}
