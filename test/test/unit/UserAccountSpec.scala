/**
 * Default (Template) Project
 * Created by andrew on 21/02/15.
 *
 */


package test.unit

import data.UserAccounts
import models.{UserProfile, UserRole}
import models.UserRole.NormalUser
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.db.DB
import play.api.test.FakeApplication
import play.api.test.Helpers._
import test._
import test.helpers.UserAccountHelper

import scala.slick.jdbc.JdbcBackend._


class UserAccountSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())
  
  "User Account" must {

      "Have associated user profiles" in {
        val user = UserAccountHelper.createUserAndProfile("TestUser","TestUser","TrustedContributor")
        database.withSession { implicit session =>
          UserAccounts.getProfile(user) must not be None
        }
      }
      "Allow for user aliases" in {
        val user = UserAccountHelper.createUserWithAlias("Andrew","Andrew","pa$$word",UserRole.valueOf("Contributor"),"Gaz Three")
        database.withSession { implicit session =>
          UserAccounts.getAliases(user).head mustEqual "Gaz Three"
        }
      }
      "Have a default alias" in pending
  }




}
