package test.integration

import models.ContentTypeMap
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import org.scalatestplus.play._
import play.api.db.DB
import play.api.test.FakeApplication
import play.api.test.Helpers._
import test.helpers.{ReactiveMongoApp, ContentHelper}
import test.integration.pages.BlogPage
import test.{EmbedMongoGlobal, TestConfig, TestGlobal}


/**
 * Created by andrew on 11/10/14.
 */
class BlogSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with BeforeAndAfterAll with ReactiveMongoApp {

  implicit override lazy val app = buildAppEmbed

  var setupDone = false

  before{
    if(!setupDone) {
      dataSetup
      setupDone = true
    }
  }

  val newsPage = new BlogPage(port)
  import newsPage._

  after {
    dataTearDown
  }

  "Blog Page" must {

    "Display a News Item" in {

      go to newsPage
      eventually{
        Items must not be empty
      }
    }

    "Only Display News Posts" in {
      go to newsPage
      eventually{
        TypeIds must contain only ContentTypeMap.get("Blog").toString
      }
    }

    "Display links to full items" in {
      go to newsPage
      eventually{
        itemLinks must not be empty
      }
    }

    "Display a Post" in {
      go to newsPage
      eventually {
        Items(0) must include ("Some Example content blah blah blah 1234567")
      }
    }

    "When in author mode must display the authors post" in {
      go to author("MC Donalds")
      authors must contain only ("MC Donalds")
    }

    "When in author mode must not display other authors posts" in {
      go to author("MC Donalds")
      authors must not contain ("Pat Shleet")
    }

  }

  def dataSetup = {

      val newsItem = ContentHelper.createPost("DOB Test News Post","MC Donalds","Some Example content blah blah blah 1234567",ContentTypeMap.get("Blog"),None)
      ContentHelper.createPost("DOB Test News Post", "MC Donalds", "Some Example content blah blah blah 1234567", ContentTypeMap.get("Blog"), None)
      ContentHelper.createPost("DOB Test News Post", "MC Donalds", "Some Example content blah blah blah 1234567", ContentTypeMap.get("Blog"), None)
      ContentHelper.createPost("DOB Test News Post", "Pat Shleet", "Some Example content blah blah blah 1234567", ContentTypeMap.get("Blog"), None)
      ContentHelper.createPost("DOB Test News Post", "MC Donalds", "Some Example content blah blah blah 1234567", ContentTypeMap.get("Blog"), None)
      val nonNewsItem =  ContentHelper.createPost( "DOB Test Music Post","MC Donalds","Some cool DoB Music",ContentTypeMap.get("Music"),None)

  }

  def dataTearDown = {}



}

