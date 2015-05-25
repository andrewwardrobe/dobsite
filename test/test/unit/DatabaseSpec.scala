package test.unit

import com.daoostinboyeez.git.GitRepo
import data.{Profiles, Content}
import models.{ ContentPost}
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.Logger

import play.api.test.FakeApplication
import play.api.test.Helpers._
import test.helpers.{UserAccountHelper, ContentHelper}
import play.api.db.DB
import scala.slick.jdbc.JdbcBackend._
import test._
class DatabaseSpec extends PlaySpec with OneServerPerSuite{

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())

  "Database" must {


    "Be able to insert and retrieve posts items" in {
      database.withSession { implicit session =>
        GitRepo.refresh
        val newsItem = ContentHelper.createPost("DOB Test News Post","MC Donalds","News Content for db spec",1,None)
        val result = Content.get
        result.head mustEqual newsItem
      }
    }

    "Be able to save and retrieve user profile" in {
      database.withSession{ implicit session =>
        val user = UserAccountHelper.createUser("Contributor","Contributor","Contributor")
        val profile = UserAccountHelper.createProfile(user.id,"some text","assests/images/crew/donalds_bw.jpg")
        val retrievedProfile  = Profiles.get(profile.id)
        retrievedProfile mustEqual retrievedProfile
      }
    }


    "Be able to save and retrieve user profile as an option" in {
      database.withSession{ implicit session =>
        val user = UserAccountHelper.createUser("Contributor","Contributor","Contributor")
        val profile = UserAccountHelper.createProfile(user.id,"some text","assests/images/crew/donalds_bw.jpg")
        val retrievedProfile  = Profiles.getAsOption(profile.id)
        retrievedProfile must not be None
      }
    }
  }


}