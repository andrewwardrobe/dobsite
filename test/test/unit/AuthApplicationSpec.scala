package test.unit

import java.util
import java.util.{Date, UUID}

import com.daoostinboyeez.git.GitRepo
import controllers.StandardAuthConfig
import data.{Profiles, Content, Users}
import models._
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.test.{FakeRequest, FakeApplication, FakeHeaders}
import play.api.test.Helpers._
import play.api.db.DB
import reactivemongo.bson.BSONObjectID
import test.helpers.{ReactiveMongoApp, ContentHelper, UserAccountHelper}
import test.{EmbedMongoGlobal, TestGlobal, TestConfig}
import scala.collection.immutable.HashMap
import scala.concurrent.Await

import jp.t2v.lab.play2.auth.test.Helpers._

/**
 * Created by andrew on 14/09/14.
 */
class AuthApplicationSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter with ScalaFutures with ReactiveMongoApp {

import scala.concurrent.duration.DurationInt


implicit override lazy val app = buildApp


object config extends StandardAuthConfig

  import models.JsonFormats._

  var user: UserAccount = _
  var userProfile :Profile = _

  "Auth Application" should {


    "Allow Contributors to create blog posts" in {
      val leek = HashMap[String,String]("thumb" -> "cum")
      val post = new Post(BSONObjectID.generate,"title",ContentTypeMap("Blog"),new Date,"Andrew","Content",Some(leek),false,None,None)
      val json = Json.toJson(post)
      val result = route(FakeRequest(POST, controllers.routes.Authorised.submitPost().url,FakeHeaders(),json).withLoggedIn(config)("Contributor")).get
      status(result) mustBe OK
    }

    "Not Allow normal users to create news posts" in {
      val leek = HashMap[String,String]("thumb" -> "cum")
      val post = new Post(BSONObjectID.generate,"title",ContentTypeMap("Blog"),new Date,"Andrew","Content",Some(leek),false,None,None)
      val json = Json.toJson(post)
      val result = route(FakeRequest(POST, controllers.routes.Authorised.submitPost().url,FakeHeaders(),json).withLoggedIn(config)("NormalUser")).get
      status(result) mustBe FORBIDDEN
    }

    "Not Allow contributors to create biography posts" in {
      val leek = HashMap[String,String]("thumb" -> "cum")
      val post = new Post(BSONObjectID.generate,"title",ContentTypeMap("Biography"),new Date,"Andrew","Content",None,false,None,None)
      val json = Json.toJson(post)
      val result = route(FakeRequest(POST, controllers.routes.Authorised.submitPost().url,FakeHeaders(),json).withLoggedIn(config)("Contributor")).get
      status(result) mustBe UNAUTHORIZED
    }

    "Prevent users editing the post of others" in {
      val origPost = ContentHelper.createPost("Dob Post","Andrew","Some test shit",ContentTypeMap("Blog"),Some(contrib._id))
      val updatedPost = origPost.copy( content = "Some New Content" ,  userId = Some(user._id))
      val json = Json.toJson(updatedPost)
      val result = route(FakeRequest(PUT, controllers.routes.Authorised.submitBlogUpdate().url,FakeHeaders(),json).withLoggedIn(config)("TrustedContributor")).get

      status(result) mustBe UNAUTHORIZED
    }

    "Allow admins edit the post of others" in {
      val origPost = ContentHelper.createPost("Dob Post","Andrew","Some test shit",ContentTypeMap("Blog"),Some(contrib._id))
      val updatedPost = origPost.copy( content = "Some New Content" )
      val json = Json.toJson(updatedPost)
      val result = route(FakeRequest(PUT, controllers.routes.Authorised.submitBlogUpdate().url,FakeHeaders(),json).withLoggedIn(config)("Administrator")).get

      status(result) mustBe OK
    }

    "Allow TrustedContributors to create biography posts" in {
      val post = new Post(BSONObjectID.generate,"title",ContentTypeMap("Biography"),new Date,"Andrew","Content",None,false,Some(user._id),None)
      val json = Json.toJson(post)
      val result = route(FakeRequest(POST, controllers.routes.Authorised.submitPost().url,FakeHeaders(),json).withLoggedIn(config)("TrustedContributor")).get
      status(result) mustBe OK
    }

    "Allow Users to update the profile" in {
      val updateProfile = userProfile.copy(about = "New user info")
      val result = route(FakeRequest(POST, controllers.routes.Authorised.updateProfile.url,FakeHeaders(),Json.toJson(updateProfile)).withLoggedIn(config)("TrustedContributor")).get
      status(result) mustBe OK
      val retrievedProfile = Profiles.findById(updateProfile.id).futureValue
      retrievedProfile.head mustEqual updateProfile
    }

    "Prevent Users from updating others profiles" in {
      val updateProfile = userProfile.copy(about = "New user info")
      val result = route(FakeRequest(POST, controllers.routes.Authorised.updateProfile.url,FakeHeaders(),Json.toJson(updateProfile)).withLoggedIn(config)("Contributor")).get
      status(result) mustBe FORBIDDEN
    }

    "Provide a route to the user profile" in {
      val result = route(FakeRequest(GET, "/profile",FakeHeaders(),"").withLoggedIn(config)("TrustedContributor")).get
      status(result) mustBe OK
    }

    "Allow users to create an alias" in {
      val result = route(FakeRequest(POST, controllers.routes.Authorised.addAlias("Leeek").url, FakeHeaders(), "").withLoggedIn(config)("TrustedContributor")).get
      status(result) mustBe OK
    }

    "Prevent adding alias if it is some ones user name" in {
      val result = route(FakeRequest(POST, controllers.routes.Authorised.addAlias("Contributor").url, FakeHeaders(), "").withLoggedIn(config)("TrustedContributor")).get
      status(result) mustBe BAD_REQUEST
    }

    "Prevent adding alias if some one already has it" in {
      val result = route(FakeRequest(POST, controllers.routes.Authorised.addAlias("Jimmy Tee").url, FakeHeaders(), "").withLoggedIn(config)("TrustedContributor")).get
      status(result) mustBe BAD_REQUEST
    }

    "Thorw a meaningful error message when on the alias limit" in {

        for (i <- 0 to user.getAliasLimit - 1) {
          Profiles.addAlias(user, s"leek${i}").futureValue
        }

        val result = route(FakeRequest(POST, controllers.routes.Authorised.addAlias("Error One").url, FakeHeaders(), "").withLoggedIn(config)("TrustedContributor")).get
        status(result) mustBe BAD_REQUEST
        contentAsString(result) must include("Alias Limit Reached")

    }
  }

  var contrib : UserAccount = null
  var admin : UserAccount = null
  var normal : UserAccount = null


  before{
    val repo = GitRepo.apply()
    repo.refresh
    admin = UserAccountHelper.createUser("Administrator","Administrator","Administrator")
    contrib = UserAccountHelper.createUser("Contributor","Contributor","Contributor")
    user = UserAccountHelper.createUser("TrustedContributor","TrustedContributor","TrustedContributor")
    userProfile = UserAccountHelper.createProfile(user._id,"A fine oostin boyee","assets/images/leek.png")
    normal = UserAccountHelper.createUser("NormalUser","NormalUser","NormalUser")
    normal = UserAccountHelper.createUserWithAlias("BigPete","BigPete@daoostinboyeez.com","BigPete","TrustedContributor","Jimmy Tee")

  }

  after{
    import scala.concurrent.duration.DurationInt
    Await.ready(Content.deleteAll,10 seconds)
    Await.ready(Profiles.deleteAll,10 seconds)
    Await.ready(Users.deleteAll,10 seconds)

  }
}
