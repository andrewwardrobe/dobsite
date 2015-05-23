package test.helpers

import data.{Profiles, UserAccounts}
import models.{UserProfile, UserAccount, UserRole}
import play.api.db.DB
import play.api.Play.current
import scala.slick.jdbc.JdbcBackend._

/**
 * Created by andrew on 03/04/15.
 */
object UserAccountHelper {

  def database = Database.forDataSource(DB.getDataSource())

  def createUser(userId: String, email :String, password: String, role: UserRole) = {
    database.withSession { implicit session =>
      val user = new UserAccount(1,  email, password,userId, role)
      val id = UserAccounts.create(user)
      new UserAccount(id,user.email,user.password,user.name,user.role)
    }
  }

  def createUser(userId: String, password: String, role: String) = {
    database.withSession { implicit session =>
      val user = new UserAccount(1,  s"$userId@daoostinboyeez.com", password,userId, UserRole.valueOf(role))
      val id = UserAccounts.create(user)
      new UserAccount(id,user.email,user.password,user.name,user.role)
    }
  }

  def createProfile(userId:Int, about:String,avatar:String) = {
    database.withSession { implicit session =>
      val profile = new UserProfile(0, userId, about,avatar)
      val id = Profiles.save(profile)
      new UserProfile(id, profile.userId, profile.about,profile.avatar)
    }
  }
}
