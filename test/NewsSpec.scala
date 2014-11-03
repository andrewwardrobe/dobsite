import java.util.Date

import models.Blog
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter}
import org.scalatestplus.play._
import play.api.db.DB
import play.api.test.FakeApplication
import play.api.test.Helpers._

import scala.collection.mutable.ListBuffer
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

  val newsPage = new NewsPage

  after {
    dataTearDown
  }

  "News Page" must {

    "Display a News Item" in {

      go to newsPage
      eventually{
        newsPage.Items must not be empty
      }
    }

    "Only Display News Posts" in {
      go to newsPage
      eventually{
        newsPage.TypeIds must contain only("1")
      }
    }
  }

  def dataSetup = {
    database.withSession { implicit session =>
      val newsItem = Blog(1, "DOB Test News Post",1,new Date(),"MC Donalds","Some Example content blah blah blah")
      val nonNewsItem =  Blog(1, "DOB Test Music Post",2,new Date(),"MC Donalds","Some cool DoB Music")
      Blog.insert(newsItem)
      Blog.insert(nonNewsItem)

    }
  }

  def dataTearDown = {}


  class NewsPage extends Page {
    val url = s"localhost:$port/news"

    def Items = cssSelector("div[id*='newsId']").findAllElements
    def TypeIds = {

      val typeIDList: ListBuffer[String] = new ListBuffer[String]()
      cssSelector("*[id*='typId']").findAllElements.toList.foreach { element =>
        typeIDList += element.attribute("value").get.toString
      }
      typeIDList.toList
    }

  }
}
