package test.unit

import com.daoostinboyeez.git.GitRepo

import data.{Profiles, Content, Users}
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures

import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}

import play.api.test.FakeApplication
import play.api.test.Helpers._
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.ReactiveMongoHelper
import test._
import test.helpers.{ReactiveMongoApp, UserAccountHelper, ContentHelper}

import scala.concurrent.Await

import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global

class UserAccountHelperSpec extends PlaySpec with OneServerPerSuite with ScalaFutures with BeforeAndAfter with ReactiveMongoApp {

  import scala.concurrent.duration.DurationInt


  implicit override lazy val app = buildAppEmbed



  "UserAccountHelper" must {
    "Be able to create an account" in {

        val user = UserAccountHelper.createUser("test", "test@test.com", "pa$$word", "NormalUser")
        info(user.toString)
        Users.findByEmail("test@test.com").map { users =>
          users must not be empty
        }
    }
  }


  before {
    //import scala.concurrent.duration.DurationInt
    //Await.ready(Content.deleteAll,10 seconds)
    //Await.ready(UserProfiles.deleteAll,10 seconds)
    //Await.ready(UserAccounts.deleteAll,10 seconds)
  }

  after {


  }
}