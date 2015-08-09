package test.unit

import com.daoostinboyeez.git.GitRepo
import data.UserAccounts
import org.scalatest.concurrent.ScalaFutures

import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.db.DB
import play.api.test.FakeApplication
import play.api.test.Helpers._
import test._
import test.helpers.{UserAccountHelper, ContentHelper}

import scala.concurrent.Await
import scala.slick.jdbc.JdbcBackend._
import scala.concurrent.duration.DurationInt

class UserAccountHelperSpec extends PlaySpec with OneServerPerSuite with ScalaFutures{

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())


  "UserAccountHelper" must {
    "Be able to create an account" in {

        UserAccountHelper.createUser("test", "test@test.com", "pa$$word", "NormalUser")
        val res = UserAccounts.findByEmail("test@test.com")
        res.futureValue must not be empty

    }
  }


}