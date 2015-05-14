package test.helpers

import data.UserAccounts
import models.{UserAccount, UserRole}
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
      UserAccounts.create(user)
    }
  }

  def createUser(userId: String, password: String, role: String) = {
    database.withSession { implicit session =>
      val user = new UserAccount(1,  s"$userId@daoostinboyeez.com", password,userId, UserRole.valueOf(role))
      UserAccounts.create(user)
    }
  }
}
