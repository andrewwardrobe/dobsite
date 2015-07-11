package data

import models.UserAlias

/**
 * Created by andrew on 09/07/15.
 */
trait UserAliasAcess {
  this: UserAliasSchema =>

  import play.api.db.slick.Config.driver.simple._

  def insert(alias: UserAlias)(implicit session: Session) = {
    userAlias.insert(alias)
  }

  def get(implicit session: Session) = {
    userAlias.list
  }

  def get(id: String)(implicit session: Session) = {
    userAlias.filter(_.id === id).list
  }
}
