package test.integration

import com.daoostinboyeez.git.GitRepo
import models.{PostTypeMap, Post}
import models.UserRole.TrustedContributor
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.scalatest.{ShouldMatchers, BeforeAndAfter, BeforeAndAfterAll}
import org.scalatestplus.play.{FirefoxFactory, OneBrowserPerSuite, OneServerPerSuite, PlaySpec}
import play.api.db.DB
import play.api.test.FakeApplication
import play.api.test.Helpers._
import test._
import test.helpers.{UserAccountHelper, PostHelper}
import test.integration.pages._

import scala.slick.jdbc.JdbcBackend._

/**
 * Created by andrew on 01/03/15.
 */
class PostEditorSpec  extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with BeforeAndAfterAll  {
  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())

  val repo = GitRepo.apply()
  var post1: Post = null
  var post2: Post = null
  var post3: Post = null

  def extraSetup = {
    database.withSession { implicit session =>

      post1 = PostHelper.createPost("DOB Test News Post","MC Donalds","ah ah blah",1)
      post2 =  PostHelper.createPost("2nd Post","MC Donalds","Jimbo jimbp",1)
      post3 = PostHelper.createPost("3rd Post","MC Donalds","Dis is Da Oostin Boyeez Leek",1)
      repo.updateFile(post1.content,"Here is some data I just changed")
    }
  }

  val editorPage = new EditorPage(port)
  val newsPage = new NewsPage(port)
  val menuBar = new MenuBar(port)

  import editorPage._

  var setupDone: Boolean = false

  def setup() = {

    val signIn = new SignInPage(port)
    if(!setupDone) {
      repo.refresh
      UserAccountHelper.createUser("Administrator","Administrator","Administrator")
      UserAccountHelper.createUser("Contributor","Contributor","Contributor")
      UserAccountHelper.createUser("TrustedContributor","TrustedContributor","TrustedContributor")
      UserAccountHelper.createUser("NormalUser","NormalUser","NormalUser")
      signIn.signin("TrustedContributor", "TrustedContributor")
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
      editorPage.addContent("some data")
      editorPage.save
      eventually{ editorPage.saveSuccessful mustEqual (true) }
    }

    "Be able to update a post" in {
      goTo (editorPage.post(post2.id))
      editorPage.addContent("leek")
      editorPage.save
      eventually{ editorPage.saveSuccessful mustEqual (true) }
    }

    "Be able to open on a particular post type" in {
      go to editorPage.posttype("Biography")
      editorPage.postType mustEqual("Biography")
    }

    "Be able to load a post" in {
      go to post(post3.id)
      title mustBe ("3rd Post")
      editorBoxText mustBe ("Dis is Da Oostin Boyeez Leek")
      postType mustBe ("News")
    }

    "Display a drop down menu for editor things" in {
      goTo (editorPage)
      eventually {menuBar.revisionsMenu .text must include ("Revisions") }

    }
  }

  "Revision List" must {
    "Display a list of revisions when there is some" in {
      goTo (editorPage.post(post1.id))

      eventually{editorPage.revisionListText.size must be (2)}
    }

    "Display a list of revisions by dates" in {
      goTo (editorPage.post(post1.id))

      eventually{
        click on id("editorMenu")
        editorPage.revisionListText(1) must include regex """\d{2}/\d{2}/\d{4}""".r
      }
    }

    "Display a list of revisions by dates with links to the revision" in {
      goTo (editorPage.post(post1.id))
      eventually{editorPage.revisionLinks must not be empty }
    }

    "load the specified revision when the link is clicked" in {
      goTo (editorPage.post(post1.id))
      eventually{
        click on id("editorMenu")
        click on id(editorPage.revisionLinks(1).attribute("id").get)
        editorPage.editorBoxText must include ("ah ah blah")
      }
    }
  }


  def addContent(content: String) = editorPage.addContent(content)
}
