package data

import models.UserProfile

/**
 * Created by andrew on 19/05/15.
 */
trait UserProfileSchema {
  import play.api.db.slick.Config.driver.simple._

  class UserProfileTable(tag: Tag) extends Table[UserProfile](tag, "Profiles") {
    //Pri Key
    def id = column[Int]("ID",O.PrimaryKey,O.AutoInc)
    def userId = column[Int]("USER_ID")
    def about = column[String]("ABOUT")
    def avatar = column[String]("AVATAR")
    def * = (id, userId,about,avatar) <> ((UserProfile.apply _).tupled, UserProfile.unapply)
    //TODO: foreign keys
    def postFK = foreignKey("USER_FK", userId,UserAccounts.accounts)(account => account.id)
  }
}
