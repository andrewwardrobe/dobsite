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


  val editorPage = new EditorPage(port)
  var setupDone: Boolean = false

  def setup() = {
    val signUp = new SignUpPage(port)
    val signIn = new SignInPage(port)
    if(!setupDone) {
      signUp.signup("andrew", "andrew@dob.com", "pa$$word")
      signIn.signin("andrew", "pa$$word")
      setupDone = true
    }

  }

  after{
    
  }


  
  "Post Editor" must {



    "Display the editor with some initial text" in {
      setup()
      goTo (editorPage)
      editorPage.editorBoxText must include ("Start Typing you post content here")
    }

    "Ba able to save a post" in {
      setup()
      goTo (editorPage)
    }

    "Display a list of revisions when there is some" in {
      setup()
      goTo (editorPage)
      editorPage.revisionList must not be empty
    }

  }
}
