package test.unit

import com.daoostinboyeez.git.GitRepo
import controllers.StandardAuthConfig
import data.{Content, UserAccounts, Profiles}
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.db.DB
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeApplication, FakeHeaders, FakeRequest}
import reactivemongo.core.protocol.QueryFlags
import test.helpers.UserAccountHelper
import test.{EmbedMongoGlobal, TestGlobal, TestConfig}
import jp.t2v.lab.play2.auth.test.Helpers._
import scala.concurrent.duration.DurationInt
import scala.concurrent.Await
import scala.slick.jdbc.JdbcBackend._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by andrew on 14/09/14.
 */
class AdminJsonApiSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter with ScalaFutures {
  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo ++ TestConfig.withEmbbededMongo, withGlobal = Some(EmbedMongoGlobal))
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
      //Wait for the upload to finish
      result.map { result =>
        Content.count(Json.obj()).futureValue must equal(1)
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
      //Wait for the upload to finish

      result.map { result =>
        Content.count(Json.obj()).futureValue must equal(1)
      }


    }

  }
  before{
    Await.ready(Content.deleteAll,10 seconds)
    Await.ready(Profiles.deleteAll,10 seconds)
    Await.ready(UserAccounts.deleteAll,10 seconds)
    val repo = GitRepo.apply()
    repo.refresh
    UserAccountHelper.createUser("Administrator","Administrator","Administrator")

  }

  after{
    Await.ready(Content.deleteAll,10 seconds)
    Await.ready(Profiles.deleteAll,10 seconds)
    Await.ready(UserAccounts.deleteAll,10 seconds)
  }
}
