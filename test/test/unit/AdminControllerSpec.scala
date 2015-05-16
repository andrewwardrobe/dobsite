package test.unit

import com.daoostinboyeez.git.GitRepo
import controllers.StandardAuthConfig
import jp.t2v.lab.play2.auth.test.Helpers._
import models.{ContentPost}
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.db.DB
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeApplication, FakeHeaders, FakeRequest}
import test.helpers.UserAccountHelper
import test.{TestConfig, TestGlobal}

import scala.slick.jdbc.JdbcBackend._

/**
 * Created by andrew on 14/09/14.
 */
class AdminControllerSpec extends PlaySpec with OneServerPerSuite{
  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())

  object config extends StandardAuthConfig

  implicit val postFormat = Json.format[ContentPost]

  "Admin Controller" should {

    "Be able to recreate the content database from the post meta held in the content repository" in pending
  }

}
