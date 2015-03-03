import com.daoostinboyeez.git.GitRepo
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter}
import org.scalatestplus.play.{FirefoxFactory, OneBrowserPerSuite, OneServerPerSuite, PlaySpec}
import play.api.db.DB
import play.api.test.{FakeRequest, FakeApplication}
import play.api.test.Helpers._
import jp.t2v.lab.play2.auth.test.Helpers._

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
      GitRepo.updateFile(firstFile.content,"Here is some data in a file I just changed")
    }
  }

  val editorPage = new EditorPage(port)
  var setupDone: Boolean = false

  def setup() = {
    GitRepo.refresh
    val signUp = new SignUpPage(port)
    val signIn = new SignInPage(port)
    if(!setupDone) {
      signUp.signup("andrew", "andrew@dob.com", "pa$$word")
      signIn.signin("andrew", "pa$$word")
      extraSetup
      setupDone = true
    }

  }

  after{
    
  }

  "Post Editor" must {

    "Display a list of revisions when there is some" in {
      setup()
      go to s"localhost:$port"
      goTo (editorPage.post(1))
      eventually{editorPage.revisionList.size must be (2)}
    }

    "Display the editor with some initial text" in {
      setup()
      goTo (editorPage)
      editorPage.editorBoxText must include ("Typing")
    }

    "Be able to save a post" in {
      setup()
      goTo (editorPage)
      //editorPage.highLightText("Start Typing your post content here")
      //editorPage.highLightText("Typing")
      editorPage.save
      eventually{ editorPage.saveSuccessful mustEqual (true) }
    }



  }
}
