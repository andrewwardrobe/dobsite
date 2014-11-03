import java.util.Date

import models.News
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter}
import org.scalatestplus.play._
import play.api.db.DB
import play.api.test.FakeApplication
import play.api.test.Helpers._

import scala.slick.jdbc.JdbcBackend._

/**
 * Created by andrew on 11/10/14.
 */
class NewsSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with BeforeAndAfterAll  {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase())
  def database = Database.forDataSource(DB.getDataSource())

  import org.scalatest.selenium.WebBrowser.Page

  before{
    dataSetup
  }


  after {
    dataTearDown
  }

  "News Page" must {

    "Display a News Item" in {
      val newsPage = new NewsPage
      go to newsPage
      eventually{
        newsPage.Items must not be empty
      }
    }
  }


  def dataSetup = {
    database.withSession { implicit session =>
      val newsItem = News(1, "DOB Test News Post",new Date(),"MC Donalds","Some Example content blah blah blah")
      News.insert(newsItem)
      val result = News.get
      result.head mustEqual newsItem
    }
  }

  def dataTearDown = {}


  class NewsPage extends Page {
    val url = s"localhost:$port/news"

    def Items = cssSelector("div[id*='newsId']").findAllElements

  }
}
