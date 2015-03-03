package test.integration

import com.daoostinboyeez.git.GitRepo
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import org.scalatestplus.play.{FirefoxFactory, OneBrowserPerSuite, OneServerPerSuite, PlaySpec}
import play.api.db.DB
import play.api.test.FakeApplication
import play.api.test.Helpers._
import test._
import test.helpers.PostHelper
import test.integration.pages.{SignUpPage, SignInPage, EditorPage}

import scala.slick.jdbc.JdbcBackend._

/**
 * Created by andrew on 01/03/15.
 */
class PostEditorSpec  extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with BeforeAndAfterAll  {
  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())

  def extraSetup = {
    database.withSession { implicit session =>

      val firstFile = PostHelper.createPost("DOB Test News Post","MC Donalds","ah ah blah",1)
      GitRepo.updateFile(firstFile.content,"Here is some data I just changed")
    }
  }

  val editorPage = new EditorPage(port)
  var setupDone: Boolean = false

  def setup() = {
    val signUp = new SignUpPage(port)
    val signIn = new SignInPage(port)
    if(!setupDone) {
      GitRepo.refresh
      signUp.signup("andrew", "andrew@dob.com", "pa$$word")
      signIn.signin("andrew", "pa$$word")
      extraSetup
      setupDone = true
    }

  }

  before{
    setup()
  }

  "Post Editor" must {
   "Display the editor with some initial text" in {
      goTo (editorPage)
      editorPage.editorBoxText must include ("Typing")
    }

    "Be able to save a post" in {
      goTo (editorPage)
      //editorPage.highLightText("Start Typing your post content here")
      //editorPage.highLightText("Typing")
      editorPage.save
      eventually{ editorPage.saveSuccessful mustEqual (true) }
    }
  }

  "Revision List" must {
    "Display a list of revisions when there is some" in {
      goTo (editorPage.post(1))
      eventually{editorPage.revisionList.size must be (2)}
    }

    "Display a list of revisions by dates" in {
      goTo (editorPage.post(1))
      eventually{editorPage.revisionList(1) must include regex """\d{2}/\d{2}/\d{4}""".r }
    }

    "load the specifed revision when the link is clicked" in {
      fail("Not Implemented")
    }
  }
}
