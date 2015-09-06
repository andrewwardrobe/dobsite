package test.unit

import models.UserRole
import models.UserRole.{NormalUser, Contributor}
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.{FakeApplication, FakeRequest}
import play.api.test.Helpers._
import test.helpers.ReactiveMongoApp
import test.{EmbedMongoGlobal, TestGlobal, TestConfig}

/**
 * Created by andrew on 14/09/14.
 */
class UserServiceSpec extends PlaySpec with OneServerPerSuite with ReactiveMongoApp{

  implicit override lazy val app = buildAppEmbed

  "User Services Controller" must {

    "Leek" in {
      UserRole.roleHasAuthority(UserRole.valueOf("TrustedContributor"),NormalUser) mustEqual true
    }



    def setup = {

    }
  }

}
