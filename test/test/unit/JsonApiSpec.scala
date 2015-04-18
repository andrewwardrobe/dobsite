package test.unit

import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.{FakeApplication, FakeRequest}
import play.api.test.Helpers._
import test.{TestGlobal, TestConfig}

/**
 * Created by andrew on 14/09/14.
 */
class JsonApiSpec extends PlaySpec with OneServerPerSuite{

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  "Json API" should {

    "Be able to get content items X at at a time" in pending

    def setup = {

    }
  }

}
