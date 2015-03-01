import java.util.Date

import models.Post$
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter}
import org.scalatestplus.play._
import play.api.{GlobalSettings, Application, Logger}
import play.api.db.DB
import play.api.test.FakeApplication
import play.api.test.Helpers._

import scala.collection.mutable.ListBuffer
import scala.slick.jdbc.JdbcBackend._

/**
 * Created by andrew on 11/10/14.
 */
class NewsSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with BeforeAndAfterAll  {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())



  before{
    dataSetup
  }

  val newsPage = new NewsPage(port)

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

    "Display a Post" in {
      go to newsPage
      eventually {
        newsPage.Items(0) must include ("Some Example content blah blah blah")
      }
    }


  }

  def dataSetup = {
    database.withSession { implicit session =>
      val newsItem = PostHelper.createPost("DOB Test News Post","MC Donalds","Some Example content blah blah blah",1)
      val nonNewsItem =  PostHelper.createPost( "DOB Test Music Post","MC Donalds","Some cool DoB Music",2)

    }
  }

  def dataTearDown = {}



}

