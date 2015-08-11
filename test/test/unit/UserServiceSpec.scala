package test.unit

import models.UserRole
import models.UserRole.{NormalUser, Contributor}
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.{FakeApplication, FakeRequest}
import play.api.test.Helpers._
import test.{EmbedMongoGlobal, TestGlobal, TestConfig}

/**
 * Created by andrew on 14/09/14.
 */
class UserServiceSpec extends PlaySpec with OneServerPerSuite{

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo ++ TestConfig.withEmbbededMongo, withGlobal = Some(EmbedMongoGlobal))
  "User Services Controller" must {


    "Leek" in {
      UserRole.roleHasAuthority(UserRole.valueOf("TrustedContributor"),NormalUser) mustEqual true
    }


    "Allow space in user name" in fail("Implement this")
    def setup = {

    }
  }

}
