package test.unit

import java.util.{Date, UUID}

import com.daoostinboyeez.git.GitRepo
import controllers.StandardAuthConfig
import data.{UserAccounts, Profiles}
import models.{UserProfile, UserAccount, ContentTypeMap, ContentPost}
import org.scalatest.BeforeAndAfter
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
class AuthApplicationSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter{
  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())

  object config extends StandardAuthConfig

  implicit val postFormat = Json.format[ContentPost]

  var user: UserAccount = _
  var userProfile :UserProfile = _

  "Auth Application" should {


    "Allow Contributors to create news posts" in {

      val json = Json.parse("""{"id":"f26c0ddc-214e-4d3a-a8c8-0deec3045e75","title":"title","postType":1,"dateCreated":"2012-04-21","author":"Andrew","content":"12345678","extraData":"{ \"thumb\":\"assets/leek.jpg\"}","isDraft":false}""")
      val result = route(FakeRequest(POST, controllers.routes.Authorised.submitBlog().url,FakeHeaders(),json).withLoggedIn(config)("Contributor")).get
      status(result) mustBe OK
    }

    "Not Allow normal users to create news posts" in {

      val json = Json.parse("""{"id":"f26c0ddc-214e-4d3a-a8c8-0deec3045e75","title":"title","postType":1,"dateCreated":"2012-04-21","author":"Andrew","content":"12345678","extraData":"{ \"thumb\":\"assets/leek.jpg\"}","isDraft":false}""")
      val result = route(FakeRequest(POST, controllers.routes.Authorised.submitBlog().url,FakeHeaders(),json).withLoggedIn(config)("NormalUser")).get
      status(result) mustBe FORBIDDEN
    }

    "Not Allow contributors to create biography posts" in {

      val json = Json.parse("""{"id":"f26c0ddc-214e-4d3a-a8c8-0deec3045e75","title":"title","postType":4,"dateCreated":"2012-04-21","author":"Andrew","content":"12345678","extraData":"{ \"thumb\":\"assets/leek.jpg\"}","isDraft":false}""")
      val result = route(FakeRequest(POST, controllers.routes.Authorised.submitBlog().url,FakeHeaders(),json).withLoggedIn(config)("Contributor")).get
      status(result) mustBe UNAUTHORIZED
    }

    "Allow TrustedContributors to create biography posts" in {

      val json = Json.parse("""{"id":"f26c0ddc-214e-4d3a-a8c8-0deec3045e75","title":"title","postType":4,"dateCreated":"2012-04-21","author":"Andrew","content":"12345hhgjhg678","extraData":"{ \"thumb\":\"assets/leek.jpg\"}","isDraft":false}""")
      val result = route(FakeRequest(POST, controllers.routes.Authorised.submitBlog().url,FakeHeaders(),json).withLoggedIn(config)("TrustedContributor")).get
      status(result) mustBe OK
    }

    "Allow Users to update the profile" in {
      val updateProfile = new UserProfile(userProfile.id,userProfile.userId,"New user info",userProfile.avatar)
      val result = route(FakeRequest(POST, controllers.routes.Authorised.updateProfile.url,FakeHeaders(),Profiles.toJson(updateProfile)).withLoggedIn(config)("TrustedContributor")).get
      status(result) mustBe OK
      database.withSession { implicit session =>
        val retrievedProfile = Profiles.get(updateProfile.id)
        retrievedProfile mustEqual updateProfile
      }
    }

    "Prevent Users from updating others profiles" in {
      val updateProfile = new UserProfile(userProfile.id,userProfile.userId,"New user info",userProfile.avatar)
      val result = route(FakeRequest(POST, controllers.routes.Authorised.updateProfile.url,FakeHeaders(),Profiles.toJson(updateProfile)).withLoggedIn(config)("Contributor")).get
      status(result) mustBe FORBIDDEN
    }

    "Provide a route to the user profile" in {
      val result = route(FakeRequest(GET, "/profile",FakeHeaders(),"").withLoggedIn(config)("TrustedContributor")).get
      status(result) mustBe OK
    }


  }
  before{
    val repo = GitRepo.apply()
    repo.refresh
    UserAccountHelper.createUser("Administrator","Administrator","Administrator")
    UserAccountHelper.createUser("Contributor","Contributor","Contributor")
    user = UserAccountHelper.createUser("TrustedContributor","TrustedContributor","TrustedContributor")
    userProfile = UserAccountHelper.createProfile(user.id,"A fine oostin boyee","assets/images/leek.png")
    UserAccountHelper.createUser("NormalUser","NormalUser","NormalUser")

  }

  after{
    database.withSession { implicit session =>
      Profiles.deleteAll
      UserAccounts.deleteAll
    }
  }
}
