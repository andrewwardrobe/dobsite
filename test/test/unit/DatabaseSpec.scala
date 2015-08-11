package test.unit

import com.daoostinboyeez.git.GitRepo
import data.{Profiles, Content}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.Logger

import play.api.test.FakeApplication
import play.api.test.Helpers._
import reactivemongo.bson.BSONObjectID
import test.helpers.{UserAccountHelper, ContentHelper}
import play.api.db.DB
import scala.slick.jdbc.JdbcBackend._
import test._
class DatabaseSpec extends PlaySpec with OneServerPerSuite with ScalaFutures{

  import models.JsonFormats._
  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo ++ TestConfig.withEmbbededMongo, withGlobal = Some(EmbedMongoGlobal))

  "Database" must {


    "Be able to insert and retrieve posts items" in {

        GitRepo.refresh
        val newsItem = ContentHelper.createPost("DOB Test News Post", "MC Donalds", "News Content for db spec", 1, None)
        val result = Content.getById(newsItem.id).futureValue
        result.head mustEqual newsItem

    }


    "Be able to save and retrieve user profile" in {
        val profile = UserAccountHelper.createProfile(BSONObjectID.generate,"some text","assests/images/crew/donalds_bw.jpg")
        val retrievedProfile  = Profiles.getById(profile.id)
        retrievedProfile.futureValue must contain (profile)
    }



  }
}