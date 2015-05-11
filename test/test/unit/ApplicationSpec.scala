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

    def setup = {

    }
  }
    //  !!!!!!!!! Todo List
    "Refactor News Post type to be Blog Post" in pending

    "Separate db schema from " in pending

  "Change biography type to have value 3" in pending

    "Try using requirejs" in pending

    "Make Gaz three page a user profile page based on a with a blog post list based on a user alias filter" in pending
          "\tMake a user profile feature" in pending
          "\tMake a user alias feature" in pending
                "\t\tOnly allow TrustedContributor and admin to have this feature" in pending

    "Switch the discography page to use the post system" in pending
          "\tGive discography a post type in the post type map" in pending
          "\tList track one by one in content then loop that in discography page" in pending
          "\tUse extra data thumb for picture" in pending
          "\tAdd in some discography specific stuff to the view page to display album as two column list or single column lis if on small screen or less than 10 tracks" in pending

    "Remove the Biography,Person and discography model types" in pending

    "Make a show in user profile check box on editor" in pending

    "Make a music page based on a music tag filter + user filter" in pending
        "\tMake a soundcloud player feature on editor" in pending

    "Refactor post to be ContentPost or content" in pending
        "\tRefactor PostTypeMap to be ContentTypeMap" in pending

    "Facility to bulk load in posts" in pending

    "Facility to restore/migrate posts from meta in the commits" in pending

    "Facility to restore/migrate user accounts, store this in the git repo some how" in pending



}
