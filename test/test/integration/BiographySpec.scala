package test.integration

import com.daoostinboyeez.git.GitRepo
import models.UserRole.TrustedContributor
import models._
import org.scalatest._
import org.scalatestplus.play._
import play.api.db.DB
import play.api.test.Helpers._
import play.api.test._
import test.helpers.{UserAccountHelper, PostHelper}
import test.{TestGlobal, TestConfig}
import test.integration.pages.{SignInPage, SignUpPage, BiographyListPage}

import scala.slick.jdbc.JdbcBackend.Database




class BiographySpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with BeforeAndAfterAll  {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())

  val repo = GitRepo.apply()

  var bio = 0

  var setupDone: Boolean = false
  val signIn = new SignInPage(port)
  val biographyPage = new BiographyListPage(port)
  def extraSetup = {
    database.withSession { implicit session =>
      PostHelper.createBiography("MC Donalds","Sample Bio 1","images/crew/donalds_bw.jpg")
      val post = PostHelper.createBiography("MC Leek","Sample Bio 2","images/crew/donalds_bw.jpg")
    }
  }
  def setup() = {
    //val signUp = new SignUpPage(port)

    if(!setupDone) {
      repo.refresh

      UserAccountHelper.createUser("andrew", "andrew@dob.com", "pa$$word",TrustedContributor)

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
      //val biographyPage = new BiographyListPage(port)
      go to biographyPage
      eventually{
        biographyPage.biographyDivs must not be empty
        biographyPage.biographyImages must not be empty
      }
    }

    "Display Biography Details" in {
      //val biographyPage = new BiographyListPage(port)
      go to biographyPage
      eventually{
        biographyPage.biographyDetails(1) must include ("Sample Bio 1")
      }
    }

    "Display a save link if the text has changed" in {
      signIn.signin("andrew", "pa$$word")
      //val biographyPage = new BiographyListPage(port)
      go to biographyPage
      eventually{
        biographyPage.updateBio(1,"MC Donalds is leek leek leek")
        biographyPage.saveButtons must not be empty
      }
      signIn.signout
    }

    "Have editable biographies if the user is a trusted contributor " in {
      signIn.signin("andrew", "pa$$word")
      go to biographyPage
      eventually{

        biographyPage.bioEditable(1) mustEqual true
        biographyPage.nameEditable(1) mustEqual true
        biographyPage.imageEditable(1) mustEqual true
      }
      signIn.signout
    }

    "Display a save sucessful indicator when successful" in {
      signIn.signin("andrew", "pa$$word")

      go to biographyPage
      biographyPage.updateBio(1,"MC Donalds is leek leek leek")
      biographyPage.saveBio(1)
      eventually{
        biographyPage.updateBio(1,"MC Donalds is leek leek leek")
        biographyPage.saveBio(1)
        biographyPage.saveSuccessfulVisible(1) mustEqual true
      }
      signIn.signout
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
