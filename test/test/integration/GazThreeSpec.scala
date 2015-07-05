package test.integration

import models.ContentTypeMap
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import org.scalatestplus.play._
import play.api.db.DB
import play.api.test.FakeApplication
import play.api.test.Helpers._
import test.helpers.ContentHelper
import test.integration.pages.{GazThreePage, BlogPage}
import test.{TestConfig, TestGlobal}

import scala.slick.jdbc.JdbcBackend._

/**
 * Created by andrew on 11/10/14.
 */
class GazThreeSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with BeforeAndAfterAll  {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())


  val gazthreePage = new GazThreePage(port)

  var setupDone = false

  before{
    if(!setupDone) {
      dataSetup
      setupDone = true
    }
  }
  def dataSetup = {
    database.withSession { implicit session =>
      val newsItem = ContentHelper.createPost("DOB Test News Post","Gaz Three","Gaz three post Leek",ContentTypeMap.get("Gaz Three"),None)
      val nonNewsItem =  ContentHelper.createPost( "DOB Test Music Post","MC Donalds","Some cool DoB Music",ContentTypeMap.get("Biography"),None)
    }
  }

  def dataTearDown = {}
  import gazthreePage._
  "Gaz Three Page" must {

    "Display a News Item" in {

      go to gazthreePage
      eventually{
        Items must not be empty
      }
    }

    "Only Display News Posts" in {
      go to gazthreePage
      eventually{
        TypeIds must contain only(ContentTypeMap.get("Gaz Three").toString)
      }
    }

    "Display links to full items" in {
      go to gazthreePage
      eventually{
        itemLinks must not be empty
      }
    }

    "Display a Post" in {
      go to gazthreePage
      eventually {
        Items(0) must include ("Gaz three post Leek")
      }
    }

    "Have have a different logo" in pending


  }





}

