package test.helpers

import java.util.UUID

import data.{UserAliasDAO, Profiles, UserAccounts}
import models.{UserAlias, UserProfile, UserAccount, UserRole}
import play.api.db.DB
import play.api.Play.current
import scala.slick.jdbc.JdbcBackend._

/**
 * Created by andrew on 03/04/15.
 */
object UserAccountHelper {

  def database = Database.forDataSource(DB.getDataSource())

  def createUser(userId: String, email :String, password: String, role: String) = {
    database.withSession { implicit session =>
      val user = new UserAccount(1, email, password, userId, role)
      val id = UserAccounts.create(user)
      new UserAccount(id, user.email, user.password, user.name, user.role)
    }
  }


  def createUser(userId: String, password: String, role: String) = {
    database.withSession { implicit session =>
      val user = new UserAccount(1, s"$userId@daoostinboyeez.com", password, userId, role)
      val id = UserAccounts.create(user)
      new UserAccount(id, user.email, user.password, user.name, user.role)
    }
  }

  def createUserAndProfile(userId: String, password: String, role: String) = {
    database.withSession { implicit session =>
      val user = new UserAccount(1, s"$userId@daoostinboyeez.com", password, userId, role)
      val id = UserAccounts.create(user)
      val profile = new UserProfile(0, id,"Some Test about","assets/images/crew/donalds_bw.jpg")
      Profiles.create(profile)

      new UserAccount(id, user.email, user.password, user.name, user.role)

    }
  }

  def createUserWithAlias(userId: String, email :String, password: String, role: String,alias: String ) = {
    database.withSession { implicit session =>
      val user = new UserAccount(1, email, password, userId, role)
      val id = UserAccounts.create(user)
      UserAliasDAO.insert(new UserAlias(UUID.randomUUID.toString,id,alias))
      new UserAccount(id, user.email, user.password, user.name, user.role)

    }
  }

  def createProfile(userId:Int, about:String,avatar:String) = {
    database.withSession { implicit session =>
      val profile = new UserProfile(0, userId, about,avatar)
      val id = Profiles.create(profile)
      new UserProfile(id, profile.userId, profile.about,profile.avatar)
    }
  }
}
