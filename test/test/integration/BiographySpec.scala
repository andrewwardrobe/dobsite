package test.integration

import models._
import org.scalatest._
import org.scalatestplus.play._
import play.api.db.DB
import play.api.test.Helpers._
import play.api.test._
import test.integration.pages.BiographyListPage

import scala.slick.jdbc.JdbcBackend.Database




class BiographySpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with BeforeAndAfterAll  {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase())
  def database = Database.forDataSource(DB.getDataSource())


  var bio = 0;

  before{
    dataSetup
  }


  after {
    dataTearDown
  }


  "Biography Pages" must {

    "Render A table containing links to available biographies" in {
      val biographyPage = new BiographyListPage(port)
      go to biographyPage
      eventually{
        biographyPage.biographyDivs must not be empty
        biographyPage.biographyImages must not be empty
      }
    }

    "Display Biography Details" in {
      val biographyPage = new BiographyListPage(port)
      go to biographyPage
      eventually{
        biographyPage.biographyDetails(bio) must include ("blah blah")
      }
    }
  }

  def dataSetup() = {
    database.withSession { implicit session =>
       bio = Biography.insert(Biography(1, "MC Donalds", 0, "images/crew/donalds_bw.jpg", "images/crew/donalds_bw.jpg","blah blah"))

    }
  }

  def dataTearDown() = {
    database.withSession { implicit session =>
        val bio = Biography.getByName("MC Donalds").head
        Biography.delete(bio.id)
    }
  }
}
