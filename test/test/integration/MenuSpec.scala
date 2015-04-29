package test.integration

import com.daoostinboyeez.git.GitRepo
import models._
import org.scalatest._
import org.scalatestplus.play._
import play.api.db.DB
import play.api.test.Helpers._
import play.api.test._
import test.helpers.UserAccountHelper
import test.integration.pages.{EditorPage, MenuBar, SignInPage, DiscographyPage}
import test.{TestConfig, TestGlobal}

import scala.slick.jdbc.JdbcBackend.Database


class MenuSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with BeforeAndAfterAll  {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())

  val signInPage = new SignInPage(port)
  val menuBar = new MenuBar(port)
  val editorPage = new EditorPage(port)

  before{

  }


  after {

  }


  "Menu Bar Pages" must {
    import signInPage._
    import menuBar._
    "Provide a list of  thes types of content editible by the user" in {
      setup
      signin("TrustedContributor","TrustedContributor")
      eventually{editLinks must not be empty}

    }

    "Provide Links to The type of content editible by the user" in {
      setup
      signin("Administrator","Administrator")

      clickEditLink("Biography")
      editorPage.postType mustEqual("Biography")

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