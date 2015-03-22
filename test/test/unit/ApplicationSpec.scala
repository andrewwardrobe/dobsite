package test.unit

import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.FakeRequest
import play.api.test.Helpers._

/**
 * Created by andrew on 14/09/14.
 */
class ApplicationSpec extends PlaySpec with OneServerPerSuite{

  "Application" should {

    "send 404 on a bad request" in {
      route(FakeRequest(GET, "/boum")) mustBe None
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

    def setup = {

    }
  }

}