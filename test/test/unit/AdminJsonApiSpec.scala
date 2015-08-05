package test.unit

import com.daoostinboyeez.git.GitRepo
import controllers.StandardAuthConfig
import data.{Content, UserAccounts, UserProfiles}
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.db.DB
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeApplication, FakeHeaders, FakeRequest}
import test.helpers.UserAccountHelper
import test.{TestGlobal, TestConfig}
import jp.t2v.lab.play2.auth.test.Helpers._

import scala.slick.jdbc.JdbcBackend._

/**
 * Created by andrew on 14/09/14.
 */
class AdminJsonApiSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter with ScalaFutures {
  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  object config extends StandardAuthConfig
  def database = Database.forDataSource(DB.getDataSource())

  "Authorises Json API" should {

    "load discography data from json" in {
      val json =
        """
          |{"discographies":[
          |  {
          |    "title":"Mit Da Queen Mutter",
          |    "tracks":["24 Stunden Deororant","KY Jam","Folks",
          |      "Replacement Hip Hop","Candle in the Draft",
          |      "Misuse May Result In Sudden Death"],
          |    "discType":"album",
          |    "artwork":"assets/images/mdqm.jpg"
          |  }
          |]}
        """.stripMargin
      val result = route(FakeRequest(POST, controllers.routes.AdminJsonApi.insertDiscographies().url,FakeHeaders(),Json.parse(json)).withLoggedIn(config)("Administrator")).get
      status(result) mustBe OK
      database.withSession { implicit session =>
        Content.count(Json.obj( )) must equal(1)
      }
    }

    "load biography data from json" in {
      val json =
        """
          |{"biographies":[{
          |    "name":"MC Donalds",
          |    "text":"<p>Some example text</p> <p>Some more stuff</p>",
          |    "thumbnail":"assets/images/crew/donalds_col.jpg"
          |  }]
          |}
        """.stripMargin
      val result = route(FakeRequest(POST, controllers.routes.AdminJsonApi.insertDiscographies().url,FakeHeaders(),Json.parse(json)).withLoggedIn(config)("Administrator")).get
      status(result) mustBe OK
      database.withSession { implicit session =>
        Content.count() must equal(1)
      }
    }

  }
  before{
    val repo = GitRepo.apply()
    repo.refresh
    UserAccountHelper.createUser("Administrator","Administrator","Administrator")

  }

  after{
    database.withSession { implicit session =>
      Content.deleteAll
      UserProfiles.deleteAll
      UserAccounts.deleteAll

    }
  }
}
