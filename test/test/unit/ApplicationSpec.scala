package test.unit

import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.{FakeApplication, FakeRequest}
import play.api.test.Helpers._
import test.{TestGlobal, TestConfig}

/**
 * Created by andrew on 14/09/14.
 */
class ApplicationSpec extends PlaySpec with OneServerPerSuite{

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  "Application" should {

    "send 404 on a bad request" in {
      route(FakeRequest(GET, "/boum/lle")) mustBe None
    }

    "render the index page" in{
      val home = route(FakeRequest(GET, "/")).get

      status(home) mustBe (OK)
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Da Oostin Boyeez")
    }

    "Get a list of post revisions" in {
      val revisions = route(FakeRequest(GET,"/json/content/1/revisions")).get
      status(revisions) mustBe (OK)
      contentType(revisions) mustBe Some("application/json")
    }

    "Send requests for  News type post to the news  full page view(post page)" in (pending)
    "Send  requests for  biographies to the biography full page view" in (pending)
    "Send  requests for  gaz three posts  to the gaz three full page view" in (pending)
    def setup = {

    }
  }

}
