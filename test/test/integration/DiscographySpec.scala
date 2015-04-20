package test.integration

import models._
import org.scalatest._
import org.scalatestplus.play._
import play.api.db.DB
import play.api.test.Helpers._
import play.api.test._
import test.{TestGlobal, TestConfig}
import test.integration.pages.DiscographyPage

import scala.slick.jdbc.JdbcBackend.Database


class DiscographySpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with BeforeAndAfterAll  {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())
  val discography = new DiscographyPage(port)

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

        discography.Modals must not be empty


        discography.Tracks must not be empty
        discography.Tracks must have length 3
      }
    }
  }

  def dataSetup() = {
    database.withSession { implicit session =>

      Discography.insert(Discography(1, "Mit Da Queen Mutter", 0, "images/mdqm.jpg"))


      val disc = Discography.getByName("Mit Da Queen Mutter").head

      Track.insert(Track(1,disc.id,1,"Replacement Hip Hop"))
      Track.insert(Track(2,disc.id,2,"Mit Da Queen Mutter"))
      Track.insert(Track(3,disc.id,3,"Leek"))
    }
  }

  def dataTearDown() = {
    database.withSession { implicit session =>
      val disc = Discography.getByName("Mit Da Queen Mutter").head

      Track.deleteByRelId(disc.id)
      Discography.delete(disc.id);
      //info(res.head.name)
    }
  }

 }