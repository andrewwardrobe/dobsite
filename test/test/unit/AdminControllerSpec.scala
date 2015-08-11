package test.unit

import com.daoostinboyeez.git.GitRepo
import controllers.StandardAuthConfig
import data.{Content, UserProfiles, UserAccounts}
import jp.t2v.lab.play2.auth.test.Helpers._
import models.{MongoPost}
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.db.DB
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeApplication, FakeHeaders, FakeRequest}
import test.helpers.UserAccountHelper
import test.{EmbedMongoGlobal, TestConfig, TestGlobal}
import scala.concurrent.duration.DurationInt

import scala.concurrent.Await
import scala.slick.jdbc.JdbcBackend._

/**
 * Created by andrew on 14/09/14.
 */
class AdminControllerSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter {
  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo ++ TestConfig.withEmbbededMongo, withGlobal = Some(EmbedMongoGlobal))
  def database = Database.forDataSource(DB.getDataSource())

  object config extends StandardAuthConfig

  import models.JsonFormats._

  "Admin Controller" should {

    "Must allow admins to reload post db tables from the repo"  in {
      val result = route(FakeRequest(GET, "/admin/reload",FakeHeaders(),"").withLoggedIn(config)("Administrator")).get
      status(result) mustBe OK
    }

    "Provide a route to a bulk uplaoder"  in {
      val result = route(FakeRequest(GET, controllers.routes.Admin.bulkUploader().url,FakeHeaders(),"").withLoggedIn(config)("Administrator")).get
      status(result) mustBe OK
    }
  }

  before{
    val repo = GitRepo.apply()
    repo.refresh
    UserAccountHelper.createUser("Administrator","Administrator","Administrator")
    UserAccountHelper.createUser("Contributor","Contributor","Contributor")
    UserAccountHelper.createUser("TrustedContributor","TrustedContributor","TrustedContributor")
    UserAccountHelper.createUser("NormalUser","NormalUser","NormalUser")

  }

  after{
    import scala.concurrent.duration.DurationInt
    Await.ready(Content.deleteAll,10 seconds)
    Await.ready(UserProfiles.deleteAll,10 seconds)
    Await.ready(UserAccounts.deleteAll,10 seconds)
  }
}
