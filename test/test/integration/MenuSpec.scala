package test.integration

import com.daoostinboyeez.git.GitRepo
import com.github.simplyscala.MongoEmbedDatabase
import data.{Users, Profiles, Content}
import models._
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import play.api.db.DB
import play.api.test.Helpers._
import play.api.test._
import test.helpers.UserAccountHelper
import test.integration.pages.{EditorPage, MenuBar, SignInPage}
import test.{EmbedMongoGlobal, TestConfig, TestGlobal}

import scala.concurrent.Await



class MenuSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with BeforeAndAfterAll  with ScalaFutures with MongoEmbedDatabase  {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo  ++ TestConfig.withEmbbededMongo, withGlobal = Some(EmbedMongoGlobal))

  val signInPage = new SignInPage(port)
  val menuBar = new MenuBar(port)
  val editorPage = new EditorPage(port)



  before{
    import scala.concurrent.duration.DurationInt
    Await.ready(Content.deleteAll,10 seconds)
    Await.ready(Profiles.deleteAll,10 seconds)
    Await.ready(Users.deleteAll,10 seconds)
    setup
  }


  after {
    signInPage.signout
    import scala.concurrent.duration.DurationInt
    Await.ready(Content.deleteAll,10 seconds)
    Await.ready(Profiles.deleteAll,10 seconds)
    Await.ready(Users.deleteAll,10 seconds)
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
    UserAccountHelper.createUserAndProfile("Administrator","Administrator","Administrator")
    UserAccountHelper.createUserAndProfile("Contributor","Contributor","Contributor")
    UserAccountHelper.createUserAndProfile("TrustedContributor","TrustedContributor","TrustedContributor")
    UserAccountHelper.createUserAndProfile("NormalUser","NormalUser","NormalUser")

  }

 }