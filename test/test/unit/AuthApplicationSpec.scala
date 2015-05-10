package test.unit

import java.util.{Date, UUID}

import com.daoostinboyeez.git.GitRepo
import controllers.StandardAuthConfig
import models.{PostTypeMap, Post, Biography}
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.test.{FakeRequest, FakeApplication, FakeHeaders}
import play.api.test.Helpers._
import play.api.db.DB
import test.helpers.UserAccountHelper
import test.{TestGlobal, TestConfig}
import scala.slick.jdbc.JdbcBackend._
import jp.t2v.lab.play2.auth.test.Helpers._

/**
 * Created by andrew on 14/09/14.
 */
class AuthApplicationSpec extends PlaySpec with OneServerPerSuite{
  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())

  object config extends StandardAuthConfig

  implicit val postFormat = Json.format[Post]

  "Auth Application" should {

    "Be able to Update a biography" in {

      database.withSession { implicit session =>
        val bio = new Biography(1, "MC Donalds", 0, "images/crew/donalds_bw.jpg", "images/crew/donalds_bw.jpg", "blah blah")
        val id = Biography.insert(bio)
        val updatedBio = new Biography(id, bio.name, bio.bioType, bio.imagePath, bio.thumbPath, "new bio text");

        val results = route(FakeRequest(POST, controllers.routes.Authorised.updateBiography().url, FakeHeaders(), updatedBio.json)).get
        status(results) mustBe (OK)
        contentType(results) mustBe Some("text/plain")
        contentAsString(results) must include (id.toString)
      }
    }

    "Allow Contributors to create news posts" in {
      setup
      val json = Json.parse("""{"id":"f26c0ddc-214e-4d3a-a8c8-0deec3045e75","title":"title","postType":1,"dateCreated":"2012-04-21","author":"Andrew","content":"12345678","extraData":"{ \"thumb\":\"assets/leek.jpg\"}","isDraft":false}""")
      val result = route(FakeRequest(POST, controllers.routes.Authorised.submitBlog().url,FakeHeaders(),json).withLoggedIn(config)("Contributor")).get
      status(result) mustBe OK
    }

    "Not Allow normal users to create news posts" in {
      setup
      val json = Json.parse("""{"id":"f26c0ddc-214e-4d3a-a8c8-0deec3045e75","title":"title","postType":1,"dateCreated":"2012-04-21","author":"Andrew","content":"12345678","extraData":"{ \"thumb\":\"assets/leek.jpg\"}","isDraft":false}""")
      val result = route(FakeRequest(POST, controllers.routes.Authorised.submitBlog().url,FakeHeaders(),json).withLoggedIn(config)("NormalUser")).get
      status(result) mustBe FORBIDDEN
    }

    "Not Allow contributors to create biography posts" in {
      setup
      val json = Json.parse("""{"id":"f26c0ddc-214e-4d3a-a8c8-0deec3045e75","title":"title","postType":4,"dateCreated":"2012-04-21","author":"Andrew","content":"12345678","extraData":"{ \"thumb\":\"assets/leek.jpg\"}","isDraft":false}""")
      val result = route(FakeRequest(POST, controllers.routes.Authorised.submitBlog().url,FakeHeaders(),json).withLoggedIn(config)("Contributor")).get
      status(result) mustBe UNAUTHORIZED
    }

    "Allow TrustedContributors to create biography posts" in {
      setup
      val json = Json.parse("""{"id":"f26c0ddc-214e-4d3a-a8c8-0deec3045e75","title":"title","postType":4,"dateCreated":"2012-04-21","author":"Andrew","content":"12345hhgjhg678","extraData":"{ \"thumb\":\"assets/leek.jpg\"}","isDraft":false}""")
      val result = route(FakeRequest(POST, controllers.routes.Authorised.submitBlog().url,FakeHeaders(),json).withLoggedIn(config)("TrustedContributor")).get
      status(result) mustBe OK
    }

    def setup = {
      val repo = GitRepo.apply()
      repo.refresh
      UserAccountHelper.createUser("Administrator","Administrator","Administrator")
      UserAccountHelper.createUser("Contributor","Contributor","Contributor")
      UserAccountHelper.createUser("TrustedContributor","TrustedContributor","TrustedContributor")
      UserAccountHelper.createUser("NormalUser","NormalUser","NormalUser")

    }
  }

}
