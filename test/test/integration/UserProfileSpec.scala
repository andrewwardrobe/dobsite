package test.integration

import com.daoostinboyeez.git.GitRepo
import data.{Content, UserAccounts, Profiles}
import models.UserAccount
import org.scalatest._
import org.scalatestplus.play._
import play.api.db.DB
import play.api.test.Helpers._
import play.api.test._
import test.helpers.{ContentHelper, UserAccountHelper}
import test.integration.pages.{ProfilePage, BiographyPage, SignInPage}
import test.{TestConfig, TestGlobal}

import scala.slick.jdbc.JdbcBackend.Database


class UserProfileSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with BeforeAndAfterAll  {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())

  lazy val repo = GitRepo.apply()

  var bio = ""

  var trust:UserAccount = _
  lazy val signInPage = new SignInPage(port)
  lazy val profilePage = new ProfilePage(port)
  import signInPage._
  import profilePage._

  def setup() = {
    UserAccountHelper.createUser("Administrator","Administrator","Administrator")
    UserAccountHelper.createUser("Contributor","Contributor","Contributor")
    trust = UserAccountHelper.createUser("TrustedContributor","TrustedContributor","TrustedContributor")
    UserAccountHelper.createProfile(trust.id,"this user is a fine member of da oostin boyeez","assets/images/crew/donalds_bw.jpg")
    UserAccountHelper.createUser("NormalUser","NormalUser","NormalUser")

  }

  before{
    //repo.refresh
    setup
  }


  after {
    dataTearDown
  }


  "User Profile Pages" must {

    "Display the users name" in {
      signin("TrustedContributor","TrustedContributor")
      go to  profilePage
      usernameText must equal ("TrustedContributor")
    }

    "Display an about section" in {

      signin("TrustedContributor","TrustedContributor")
      go to  profilePage
      aboutText must include ("this user is a fine member of da oostin boyeez")
    }

    "Display a user avatar" in {
      signin("TrustedContributor","TrustedContributor")
      go to profilePage
      avatarDisplayed mustBe true
    }

    "Allow the user to edit the about" in {
      signin("TrustedContributor","TrustedContributor")
      go to profilePage
      toogleEditMode
      aboutEditable mustBe true
    }

    "Allow the user to edit the avatar" in {
      signin("TrustedContributor","TrustedContributor")
      go to profilePage
      toogleEditMode
      avatarEditable mustBe true
    }

    "Display a save button when changes have been made to the profile" in {
      signin("TrustedContributor","TrustedContributor")
      go to profilePage
      toogleEditMode
      updateAbout("New about text")
      saveButton mustBe 'displayed
    }

    "Save changes to the profile" in {
      signin("TrustedContributor","TrustedContributor")
      go to profilePage
      toogleEditMode
      updateAbout("New about text")
      save
      saveSuccess mustBe 'displayed
    }

    "Be able to update a profile" in {
      signin("TrustedContributor","TrustedContributor")
      go to profilePage

    }

    "Display a list of links to the users posts" in {
      ContentHelper.createPost("Test Post 1","MC Donalds","Sample Post Content 1",1,Some(trust.id) )
      ContentHelper.createPost("Test Post 2","MC Donalds","Sample Post Content 2",1,Some(trust.id) )
      ContentHelper.createPost("Test Post 3","MC Donalds","Sample Post Content 3",2,Some(trust.id) )
      signin("TrustedContributor","TrustedContributor")
      go to profilePage

      postLinks must not be empty
      database.withSession{implicit session => Content.deleteAll}
    }

    "Display a list links of the edit pages of the users posts" in {
      ContentHelper.createPost("Test Post 1","MC Donalds","Sample Post Content 1",1,Some(trust.id) )
      ContentHelper.createPost("Test Post 2","MC Donalds","Sample Post Content 2",1,Some(trust.id) )
      ContentHelper.createPost("Test Post 3","MC Donalds","Sample Post Content 3",2,Some(trust.id) )
      signin("TrustedContributor","TrustedContributor")
      go to profilePage

      editLinks must not be empty
      database.withSession{implicit session => Content.deleteAll}
    }

    "Display a list links of the edit pages of the users drafts" in {
      ContentHelper.createDraft("Test Post 1","MC Donalds","Sample Post Content 1",1,Some(trust.id) )
      ContentHelper.createDraft("Test Post 2","MC Donalds","Sample Post Content 2",1,Some(trust.id) )
      ContentHelper.createDraft("Test Post 3","MC Donalds","Sample Post Content 3",2,Some(trust.id) )
      signin("TrustedContributor","TrustedContributor")
      go to profilePage

      draftLinks must not be empty
      database.withSession{implicit session => Content.deleteAll}
    }

  }

  def dataSetup() = {

    database.withSession { implicit session =>

    }
  }

  def dataTearDown() = {
    database.withSession { implicit session =>

      Profiles.deleteAll
      UserAccounts.deleteAll
    }
  }
}
