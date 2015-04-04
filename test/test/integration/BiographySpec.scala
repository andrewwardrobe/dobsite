package test.integration

import com.daoostinboyeez.git.GitRepo
import models._
import org.scalatest._
import org.scalatestplus.play._
import play.api.db.DB
import play.api.test.Helpers._
import play.api.test._
import test.helpers.PostHelper
import test.{TestGlobal, TestConfig}
import test.integration.pages.{SignInPage, SignUpPage, BiographyListPage}

import scala.slick.jdbc.JdbcBackend.Database




class BiographySpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with BeforeAndAfterAll  {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())

  val repo = GitRepo.apply()

  var bio = 0

  var setupDone: Boolean = false

  def extraSetup = {
    database.withSession { implicit session =>
      PostHelper.createBiography("MC Donalds","Sample Bio 1","images/crew/donalds_bw.jpg")
      val post = PostHelper.createBiography("MC Leek","Sample Bio 2","images/crew/donalds_bw.jpg")
    }
  }
  def setup() = {
    val signUp = new SignUpPage(port)
    val signIn = new SignInPage(port)
    if(!setupDone) {
      repo.refresh
      signUp.signup("andrew", "andrew@dob.com", "pa$$word")
      signIn.signin("andrew", "pa$$word")
      extraSetup
      setupDone = true
    }

  }
  before{
    //repo.refresh
    setup
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
        biographyPage.biographyDetails(1) must include ("Sample Bio 1")
      }
    }

    "Display a save link if the user is at least a founder" in {
      val biographyPage = new BiographyListPage(port)
      go to biographyPage
      eventually{
        biographyPage.saveButtons must not be empty
      }
    }
  }

  def dataSetup() = {

    database.withSession { implicit session =>

    }
  }

  def dataTearDown() = {
    database.withSession { implicit session =>

    }
  }
}
