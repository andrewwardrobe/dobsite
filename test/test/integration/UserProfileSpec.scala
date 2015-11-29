package test.integration

import com.daoostinboyeez.git.GitRepo
import data.{Profiles, Content, Users}
import models.{UserRole, UserAccount}
import org.scalatest._
import org.scalatestplus.play._

import play.api.test.Helpers._
import play.api.test._
import test.helpers.{ReactiveMongoApp, ContentHelper, UserAccountHelper}
import test.integration.pages.{ProfilePage, BiographyPage, SignInPage}
import test.{EmbedMongoGlobal, TestConfig, TestGlobal}

import scala.concurrent.Await



class UserProfileSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with BeforeAndAfterAll  with ReactiveMongoApp {

  implicit override lazy val app = buildAppEmbed

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
    trust = UserAccountHelper.createUserWithAlias("TrustedContributor", "TrustedContributor@dob.com", "TrustedContributor", "TrustedContributor", "Da Oostin Boyeez","this user is a fine member of da oostin boyeez")
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
      eventually {toogleEditMode}
      eventually {aboutEditable mustBe true}
    }

    "Allow the user to edit the avatar" in {
      signin("TrustedContributor","TrustedContributor")
      go to profilePage
      eventually {toogleEditMode}
      eventually {avatarEditable mustBe true}
    }

    "Allow the user to upload an avatar" in {
      signin("TrustedContributor","TrustedContributor")
      go to profilePage
      eventually {toogleEditMode}
      uploadAvatar("public/images/skull.png")
      eventually{ avatarLocation must include regex "images/uploaded" }
    }

    "List aliases" in {
      signin("TrustedContributor", "TrustedContributor")
      go to profilePage
      eventually {
        aliasList must contain("Da Oostin Boyeez")
      }
    }
    "Allow Trusted Contributors to add an alias" in {
      signin("TrustedContributor", "TrustedContributor")
      go to profilePage
      addAlias("Gaz Three")
      eventually {
        aliasList must contain("Gaz Three")
      }
    }
    "Display a save button when changes have been made to the profile" in {
      signin("TrustedContributor","TrustedContributor")
      go to profilePage
      eventually {
        toogleEditMode
        updateAbout("New about text")
        saveButton mustBe 'displayed
      }
    }

    "Save changes to the profile" in {
      signin("TrustedContributor","TrustedContributor")
      go to profilePage
      toogleEditMode
      updateAbout("New about text")
      eventually {save }
      saveSuccess mustBe 'displayed
    }

    "Be able to update a profile" in {
      signin("TrustedContributor","TrustedContributor")
      go to profilePage

    }



    "Display a list of links to the users posts" in {
      ContentHelper.createPost("Test Post 1","MC Donalds","Sample Post Content 1",1,Some(trust._id) )
      ContentHelper.createPost("Test Post 2","MC Donalds","Sample Post Content 2",1,Some(trust._id) )
      ContentHelper.createPost("Test Post 3","MC Donalds","Sample Post Content 3",2,Some(trust._id) )
      signin("TrustedContributor","TrustedContributor")
      go to profilePage

      eventually {postLinks must not be empty}

    }

    "Display a list links of the edit pages of the users posts" in {
      ContentHelper.createPost("Test Post 1","MC Donalds","Sample Post Content 1",1,Some(trust._id) )
      ContentHelper.createPost("Test Post 2","MC Donalds","Sample Post Content 2",1,Some(trust._id) )
      ContentHelper.createPost("Test Post 3","MC Donalds","Sample Post Content 3",2,Some(trust._id) )
      signin("TrustedContributor","TrustedContributor")
      go to profilePage

      eventually { editLinks must not be empty }

    }

    "Display a list links of the edit pages of the users drafts" in {
      ContentHelper.createDraft("Test Post 1","MC Donalds","Sample Post Content 1",1,Some(trust._id) )
      ContentHelper.createDraft("Test Post 2","MC Donalds","Sample Post Content 2",1,Some(trust._id) )
      ContentHelper.createDraft("Test Post 3","MC Donalds","Sample Post Content 3",2,Some(trust._id) )
      signin("TrustedContributor","TrustedContributor")
      go to profilePage

      eventually { draftLinks must not be empty }

    }

    "Show the added alias when successful" in {
      go to profilePage
      addAlias("Jimbo Jambo")
      eventually { aliasList must contain("Jimbo Jambo") }
    }

  }

  def dataSetup() = {


  }

  def dataTearDown() = {
    import scala.concurrent.duration.DurationInt
    Await.ready(Content.deleteAll,10 seconds)
    Await.ready(Profiles.deleteAll,10 seconds)
    Await.ready(Users.deleteAll,10 seconds)

  }
}
