package test.integration

import data.Content
import models._
import org.scalatest._
import org.scalatestplus.play._
import play.api.db.DB
import play.api.test.Helpers._
import play.api.test._
import test.helpers.ContentHelper
import test.{EmbedMongoGlobal, TestGlobal, TestConfig}
import test.integration.pages.DiscographyPage

import scala.concurrent.Await
import models.JsonFormats._

class DiscographySpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with BeforeAndAfterAll  {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo  ++ TestConfig.withEmbbededMongo, withGlobal = Some(EmbedMongoGlobal))
  val discography = new DiscographyPage(port)

  var bio : Post = null
  before{
    dataSetup
  }


  after {
    dataTearDown()
  }


  "Discography Pages" must {

    "Render The Index Page" in {
      go to (s"http://localhost:$port/")
      pageTitle mustBe "Da Oostin Boyeez"
    }





    "Render Modals divs" in {
      go to discography

      //implicitlyWait(Span(10, Seconds))

      eventually {
        discography.Tracks must not be empty
        discography.Modals must not be empty
      }
    }
    "Render Albums from AJAX calls" in {

      go to discography
      eventually {
        discography.Albums must not be empty
      }
    }
  }

  def dataSetup() = {
      val content =
        """
          |<ol>
          | <li>Replacement Hip Hop</li>
          | <li>Mit Da Queen Mutter</li>
          | <li>Leek</li>
          |</ol>
        """.stripMargin
      bio = ContentHelper.createDiscographyItem("Mit Da Queen Mutter",content,"assets/images/mdqm.jpg","Album",None)
  }

  def dataTearDown() = {
      import scala.concurrent.duration.DurationInt
      Await.result(Content.delete(bio._id.stringify), 10 seconds)
  }

 }