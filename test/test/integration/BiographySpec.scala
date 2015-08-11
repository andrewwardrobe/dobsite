package test.integration

import com.daoostinboyeez.git.GitRepo
import models.UserRole.TrustedContributor
import models._
import org.scalatest._
import org.scalatestplus.play._
import play.api.db.DB
import play.api.test.Helpers._
import play.api.test._
import reactivemongo.bson.BSONObjectID
import test.helpers.{UserAccountHelper, ContentHelper}
import test.{EmbedMongoGlobal, TestGlobal, TestConfig}
import test.integration.pages.{SignInPage, SignUpPage, BiographyPage}






class BiographySpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with BeforeAndAfterAll  {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo ++ TestConfig.withEmbbededMongo, withGlobal = Some(EmbedMongoGlobal))


  lazy val repo = GitRepo.apply()

  var bio = ""

  var setupDone: Boolean = false
  lazy val signIn = new SignInPage(port)
  lazy val biographyPage = new BiographyPage(port)
  import biographyPage._

  def extraSetup = {
      ContentHelper.createBiography("MC Donalds","Sample Bio 1","assets/images/crew/donalds_bw.jpg",None)
      val post = ContentHelper.createBiography("MC Leek","Sample Bio 2","assests/images/crew/donalds_bw.jpg",None)
      bio = post._id.stringify
  }
  def setup() = {
    //val signUp = new SignUpPage(port)

    if(!setupDone) {
      repo.refresh


      extraSetup
      setupDone = true
    }

    UserAccountHelper.createUser("andrew", "pa$$word","TrustedContributor")
    UserAccountHelper.createUser("Administrator","Administrator","Administrator")
    UserAccountHelper.createUser("Contributor","Contributor","Contributor")
    UserAccountHelper.createUser("TrustedContributor","TrustedContributor","TrustedContributor")
    UserAccountHelper.createUser("NormalUser","NormalUser","NormalUser")
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
      //val biographyPage = new BiographyPage(port)
      go to biographyPage
      eventually{
        biographyDivs must not be empty
        biographyImages must not be empty
      }
    }

    "Display Biography Details" in {
      //val biographyPage = new BiographyPage(port)
      go to biographyPage
      eventually{
        biographyDetails(bio) must include ("Sample Bio 2")
      }
    }

    "Display a save link if the text has changed" in {
      signIn.signin("TrustedContributor", "TrustedContributor")
      //val biographyPage = new BiographyPage(port)
      go to biographyPage
      updateBio(bio,"MC Donalds is leek leek leek")
      eventually{
        saveButtons must not be empty
      }
      signIn.signout
    }

    "Have editable biographies if the user is a trusted contributor " in {
      signIn.signin("TrustedContributor", "TrustedContributor")
      go to biographyPage
      eventually{
        editButtons must not be empty
      }
      signIn.signout
    }

    "Have biographies that arent editable when the edit mode isnt on" in {
      go to biographyPage
      eventually{
        bioEditable(bio) mustEqual false
        nameEditable(bio) mustEqual false
        imageEditable(bio) mustEqual false
      }

    }

    "Have biographies that become editable when the edit mode is on" in {
      signIn.signin("TrustedContributor", "TrustedContributor")
      go to biographyPage
      eventually{
        clickOnEditButton(bio)
        bioEditable(bio) mustEqual true
        nameEditable(bio) mustEqual true
        imageEditable(bio) mustEqual true
      }
      signIn.signout
    }

    "Display a save sucessful indicator when successful" in {
      signIn.signin("TrustedContributor", "TrustedContributor")

      go to biographyPage
      clickOnEditButton(bio)
      updateBio(bio,"MC Donalds is leek leek leek")
      saveBio(bio)
      eventually{
        saveSuccessfulVisible(bio) mustEqual true
      }
      signIn.signout
    }

  }

  def dataSetup() = {

  }

  def dataTearDown() = {
  }
}
