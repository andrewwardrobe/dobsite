package data

import models.UserProfile


/**
 * Created by andrew on 19/05/15.
 */
trait UserProfileAccess { this: UserProfileSchema =>
  import play.api.db.slick.Config.driver.simple._
  val profiles = TableQuery[UserProfileTable]

  //private val byId = t.createFinderBy( _.id )
  def get(id: Int)(implicit session: Session) = profiles.filter(_.id === id).first

  def getAsOption(id:Int)(implicit session: Session) = profiles.filter(_.id === id).firstOption

  def getByUserId(userId:Int)(implicit session: Session) = profiles.filter(_.userId === userId).firstOption

  def save(account: UserProfile)(implicit session: Session) = profiles returning profiles.map(_.id) += account
}
