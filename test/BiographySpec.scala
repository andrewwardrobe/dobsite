
import org.openqa.selenium.{WebElement, By}
import org.scalatest.time.{Seconds, Span}
import org.scalatest._
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play._

import play.api.Play.current
import play.api.db.DB
import play.api.db.slick.Config.driver.simple.Session
import scala.slick.jdbc.JdbcBackend.Database
import models._


class BiographySpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with BeforeAndAfterAll  {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase())
  def database = Database.forDataSource(DB.getDataSource())


  before{
    dataSetup
  }


  after {
    dataTearDown
  }


  "Biography Pages" must {

    "Render A table containing links to available biographies" in {
      go to (s"http://localhost:$port/biography")
      eventually{
        val biographyCells = cssSelector("td[id*='bioCell']").findAllElements
        biographyCells must not be empty

        val bioLinks = cssSelector("td > a[id*='bioLink']").findAllElements
        bioLinks must not be empty

        val bioImages = cssSelector("td > img[id*='bioImage']").findAllElements
        bioImages must not be empty


      }
    }

    "Display Biography Details" in {
      go to (s"http://localhost:$port/biography")
      eventually{
        click on cssSelector("td > a[id*='bioLink']").findElement.get
      }
      eventually{
        val biographyDiv = cssSelector("*[id*='bioText']").findElement
        biographyDiv must not be empty
        val biographyText = biographyDiv.get.text
        biographyText must include ("MC Donalds")
        biographyText must include ("blah blah")
        val biographyImage = cssSelector("*[id*='bioText']").findElement
        biographyDiv must not be empty
      }

    }
  }

  def dataSetup() = {
    database.withSession { implicit session =>
       Biography.insert(Biography(1, "MC Donalds", 0, "images/crew/donalds_bw.jpg", "images/crew/donalds_bw.jpg","blah blah"))

    }
  }

  def dataTearDown() = {
    database.withSession { implicit session =>
        val bio = Biography.getByName("MC Donalds").head
        Biography.delete(bio.id)
    }
  }
}
