package test.integration

import com.daoostinboyeez.git.GitRepo
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

  lazy val signInPage = new SignInPage(port)
  lazy val profilePage = new ProfilePage(port)
  import signInPage._
  import profilePage._

  def setup() = {
    UserAccountHelper.createUser("Administrator","Administrator","Administrator")
    UserAccountHelper.createUser("Contributor","Contributor","Contributor")
    val trust = UserAccountHelper.createUser("TrustedContributor","TrustedContributor","TrustedContributor")
    UserAccountHelper.createProfile(trust.id,"this user is a fine member of da oostin boyeez")
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
      about must include ("this user is a fine member of da oostin boyeez")
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
