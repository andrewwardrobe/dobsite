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

    "render the about page" in{
      val home = route(FakeRequest(GET, "/about")).get

      status(home) mustBe (OK)
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("About")
    }

    "Get a list of post revisions" in {
      val revisions = route(FakeRequest(GET,"/json/content/1/revisions")).get
      status(revisions) mustBe (OK)
      contentType(revisions) mustBe Some("application/json")
    }

    def setup = {

    }
  }
  //Todo: do this in order
  "Make a soundcloud player feature on editor" in pending
  "Code section in editor" in pending


  "Front page" in pending
  "User login page" in pending
  "Sort out the font spacing etc" in pending

  "Change biography type to have value 3" in pending
    "Facility to restore/migrate posts from meta in the commits" in pending
    "\tRestore Tags" in pending

  "Make a music page based on a music tag filter + user filter" in pending
  "Facility to restore/migrate user accounts, store this in the git repo some how" in pending
  "Deployemt + migrate process" in pending

}
