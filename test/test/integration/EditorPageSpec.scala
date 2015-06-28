package test.integration

import com.daoostinboyeez.git.GitRepo
import models.{ContentTypeMap, ContentPost}
import models.UserRole.TrustedContributor
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.scalatest.{ShouldMatchers, BeforeAndAfter, BeforeAndAfterAll}
import org.scalatestplus.play.{FirefoxFactory, OneBrowserPerSuite, OneServerPerSuite, PlaySpec}
import play.api.db.DB
import play.api.test.FakeApplication
import play.api.test.Helpers._
import test._
import test.helpers.{UserAccountHelper, ContentHelper}
import test.integration.pages._

import scala.slick.jdbc.JdbcBackend._

/**
 * Created by andrew on 01/03/15.
 */
class EditorPageSpec  extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with BeforeAndAfterAll  {
  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())

  lazy val repo = GitRepo.apply()
  var post1: ContentPost = null
  var post2: ContentPost = null
  var post3: ContentPost = null
  var post4: ContentPost = null

  def extraSetup = {
    database.withSession { implicit session =>

      post1 = ContentHelper.createPost("DOB Test News Post","MC Donalds","ah ah blah",1,None)
      post2 =  ContentHelper.createPost("2nd Post","MC Donalds","Jimbo jimbp",1,None)
      post3 = ContentHelper.createPost("3rd Post","MC Donalds","Dis is Da Oostin Boyeez Leek",1,None)
      post4 = ContentHelper.createDraft("4th Post","MC Donalds","Jambo jimbo Leek",1,None)
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

  "Editor" must {
   "Display the editor with some initial text" in {
      goTo (editorPage)
      editorPage.editorBoxText must include ("Typing")
    }

    "Display a notification when a post is new" in {
      go to editorPage

      newAlertVisible mustEqual true
    }


    "Display a notification when a post is in draft" in {
      goTo (editorPage.post(post4.id))

      eventually{draftAlertVisible mustEqual true}
    }

    "Display a notification when a post is live" in {
      goTo (editorPage.post(post2.id))
      eventually{liveAlertVisible mustEqual true}
    }
    "Display a notification when a post has unsaved changes" in {
      go to editorPage
      addContent("some data")
      eventually {unsavedAlertVisible mustEqual true }
    }

    "Display a notification when a post is moving from live to draft on next save" in {
      goTo (editorPage.post(post2.id))
      toggleDraftMode
      eventually{liveToDraftAlertVisible mustEqual true}
    }
    "Display a notification when a post is moving from draft to live on next save" in {
      goTo (editorPage.post(post4.id))
      toggleDraftMode
      eventually{draftToLiveAlertVisible mustEqual true}
    }

    "Be in draft mode by default" in {
      go to editorPage
      draftMode mustEqual true
    }

    "Be able to toggle draft mode" in {
      go to editorPage
      toggleDraftMode
      draftMode mustEqual false
    }

    "Be able to save a post" in {
      goTo (editorPage)
      editorPage.addContent("some data")
      editorPage.save
      eventually{ editorPage.saveSuccessful mustEqual true }
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
      title mustBe "3rd Post"
      editorBoxText mustBe ("Dis is Da Oostin Boyeez Leek")
      postType mustBe ("News")
    }

    "Display a drop down menu for editor things" in {
      goTo (editorPage)
      eventually {menuBar.revisionsMenu .text must include ("Revisions") }

    }

    "Warn when switching revisions if there is unsaved changes" in pending
    "Warn when viewing a revision" in {
      goTo (editorPage.post(post1.id))
      click on id("editorMenu")
      click on id(editorPage.revisionLinks(1).attribute("id").get)
      revisionAlertVisible mustEqual true

    }

    "Be able to save tags" in {
      val postPage = new PostPage(port)
      go to post(post3.id)
      addTags("Leek,Sheek")
      save
      go to postPage.post(post3.id)
      postPage.tagList must contain allOf ("Leek","Sheek")
    }


    "Warn when making changes to revisions" in pending

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
      click on id("editorMenu")
      click on id(editorPage.revisionLinks(1).attribute("id").get)
      eventually{
        editorPage.editorBoxText must include ("ah ah blah")
      }
    }


  }


  def addContent(content: String) = editorPage.addContent(content)
}
