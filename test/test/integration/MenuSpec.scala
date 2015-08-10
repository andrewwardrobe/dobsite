package test.integration

import com.daoostinboyeez.git.GitRepo
import data.{UserAccounts, UserProfiles, Content}
import models._
import org.scalatest._
import org.scalatestplus.play._
import play.api.db.DB
import play.api.test.Helpers._
import play.api.test._
import test.helpers.UserAccountHelper
import test.integration.pages.{EditorPage, MenuBar, SignInPage}
import test.{TestConfig, TestGlobal}

import scala.concurrent.Await
import scala.slick.jdbc.JdbcBackend.Database


class MenuSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with BeforeAndAfterAll  {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())

  val signInPage = new SignInPage(port)
  val menuBar = new MenuBar(port)
  val editorPage = new EditorPage(port)



  before{
    import scala.concurrent.duration.DurationInt
    Await.ready(Content.deleteAll,10 seconds)
    Await.ready(UserProfiles.deleteAll,10 seconds)
    Await.ready(UserAccounts.deleteAll,10 seconds)
    setup
  }


  after {
    signInPage.signout
    import scala.concurrent.duration.DurationInt
    Await.ready(Content.deleteAll,10 seconds)
    Await.ready(UserProfiles.deleteAll,10 seconds)
    Await.ready(UserAccounts.deleteAll,10 seconds)
  }


  "Menu Bar Pages" must {
    import signInPage._
    import menuBar._
    "Provide a list of  thes types of content editible by the user" in {
      signin("TrustedContributor","TrustedContributor")
      eventually{editLinks must not be empty}

    }

    "Provide Links to The type of content editible by the user" in {
      signin("Administrator","Administrator")

      clickEditLink("Biography")
      editorPage.postType mustEqual("Biography")

    }

    "Provide a link to the user profile page" in {
      signin("Administrator","Administrator")
      clickOnUserMenu
      profileLink must be('displayed)
    }
  }

  def setup = {
    val repo = GitRepo.apply()
    repo.refresh
    UserAccountHelper.createUser("Administrator","Administrator","Administrator")
    UserAccountHelper.createUser("Contributor","Contributor","Contributor")
    UserAccountHelper.createUser("TrustedContributor","TrustedContributor","TrustedContributor")
    UserAccountHelper.createUser("NormalUser","NormalUser","NormalUser")

  }

 }