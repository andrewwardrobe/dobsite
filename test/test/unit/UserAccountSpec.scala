/**
 * Default (Template) Project
 * Created by andrew on 21/02/15.
 *
 */


package test.unit

import data.{Content, Profiles, Users}
import models.{UserProfile, UserRole}
import models.UserRole.NormalUser
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.db.DB
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeApplication
import play.api.test.Helpers._
import play.modules.reactivemongo.ReactiveMongoModule
import test._
import test.helpers.{ReactiveMongoApp, UserAccountHelper}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class UserAccountSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter with ScalaFutures with ReactiveMongoApp {

  import scala.concurrent.duration.DurationInt

  implicit override lazy val app = buildAppEmbed




  "User Account" must {

      "Have associated user profiles" in {
        val user = UserAccountHelper.createUserAndProfile("TestUser","TestUser","TrustedContributor")

        Profiles.findByUserId(user._id) must not be None

      }
      "Allow for user aliases" in {
        val user = UserAccountHelper.createUserWithAlias("Andrew","Andrew","pa$$word","Contributor","Gaz Three")

         Profiles.getAliasesAsString(user).futureValue.head mustEqual "Gaz Three"

      }

    "Be able an assign an alias to a user" in {
      val user = UserAccountHelper.createUserAndProfile("TestUser", "TestUser", "TrustedContributor")
      val inserted = Profiles.addAlias(user, "Leek")
      whenReady(inserted) { result =>
        val futureProfile = Profiles.findByUserId(user._id).futureValue
        futureProfile.head.aliases.getOrElse(List("Failed")) must contain("Leek")
      }
    }

    "Prevent reuse of alias'" in {
      UserAccountHelper.createUserWithAlias("Andrew", "Andrew", "pa$$word", "Contributor", "Leek")
      val user = UserAccountHelper.createUserAndProfile("TestUser", "TestUser", "TrustedContributor")

      Profiles.addAlias(user, "Leek")
      Profiles.getAliasesAsString(user).futureValue must not contain "Leek"

    }

    "Prevent use of someone else's username as an alias" in {
      UserAccountHelper.createUserWithAlias("Andrew", "Andrew", "pa$$word", "Contributor", "Leek")
      val user = UserAccountHelper.createUserAndProfile("TestUser", "TestUser", "TrustedContributor")

      Profiles.addAlias(user, "Andrew")
      Profiles.getAliasesAsString(user).futureValue must not contain "Andrew"

    }
  }

  after {
    Await.ready(Profiles.deleteAll,10 seconds)
    Await.ready(Users.deleteAll,10 seconds)
  }


}
