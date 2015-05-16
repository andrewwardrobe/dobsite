package test.unit

import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.Helpers._
import play.api.test.{FakeApplication, FakeHeaders, FakeRequest}
import test.{TestGlobal, TestConfig}

/**
 * Created by andrew on 14/09/14.
 */
class AuthJsonApiSpec extends PlaySpec with OneServerPerSuite{
  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  "Authorises Json API" should {



    def setup = {

    }
  }

}
