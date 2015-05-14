package test.integration

import data.Posts
import models._
import org.scalatest._
import org.scalatestplus.play._
import play.api.db.DB
import play.api.test.Helpers._
import play.api.test._
import test.helpers.PostHelper
import test.{TestGlobal, TestConfig}
import test.integration.pages.DiscographyPage

import scala.slick.jdbc.JdbcBackend.Database


class DiscographySpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with BeforeAndAfterAll  {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())
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



    "Render Albums from AJAX calls" in {

      go to discography
      eventually {
        discography.Albums must not be empty
      }
    }

    "Render Modals divs" in {
      go to discography

      //implicitlyWait(Span(10, Seconds))

      eventually {
        discography.Tracks must not be empty
        discography.Modals must not be empty
      }
    }
  }

  def dataSetup() = {
    database.withSession { implicit session =>

      val content =
        """
          |<ol>
          | <li>Replacement Hip Hop</li>
          | <li>Mit Da Queen Mutter</li>
          | <li>Leek</li>
          |</ol>
        """.stripMargin
      bio = PostHelper.createDiscographyItem("Mit Da Queen Mutter",content,"assets/images/mdqm.jpg","Album")

    }
  }

  def dataTearDown() = {
    database.withSession { implicit session =>
      Posts.delete(bio.id)
    }
  }

 }