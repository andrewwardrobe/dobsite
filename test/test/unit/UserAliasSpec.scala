package test.unit

import java.util.UUID

import data.UserAliasDAO
import models.{UserAlias, UserRole}
import models.UserRole.NormalUser
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.db.DB
import play.api.test.FakeApplication
import play.api.test.Helpers._
import test._
import test.helpers.UserAccountHelper

import scala.slick.jdbc.JdbcBackend._


class UserAliasSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))

  def database = Database.forDataSource(DB.getDataSource())
  "User Alias" must {

    "Be able to insert and retrieve a UserAlias from the DB " in {
      val user = UserAccountHelper.createUser("Andrew","pa$$word","Contributor")
      val id = UUID.randomUUID.toString
      val userAlias = new UserAlias(id,user.id,"Jimbo")

      database.withSession { implicit session =>
        UserAliasDAO.insert(userAlias)
        UserAliasDAO.get(id).head mustEqual userAlias
      }
    }
  }




}