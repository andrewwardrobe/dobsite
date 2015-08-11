package test.integration

import com.daoostinboyeez.git.GitRepo
import com.github.simplyscala.{MongodProps, MongoEmbedDatabase}
import models.{Post, ContentTypeMap}
import models.UserRole.TrustedContributor
import org.scalatest.{Matchers, BeforeAndAfter, BeforeAndAfterAll}
import org.scalatestplus.play.{FirefoxFactory, OneBrowserPerSuite, OneServerPerSuite, PlaySpec}

import play.api.test.FakeApplication
import play.api.test.Helpers._
import test._
import test.helpers.{ContentHelper, UserAccountHelper}
import test.integration.pages.PostPage


/**
 * Created by andrew on 01/03/15.
 */
class PostPageSpec  extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with MongoEmbedDatabase {
  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo ++ TestConfig.withEmbbededMongo, withGlobal = Some(EmbedMongoGlobal))




  lazy val repo = GitRepo.apply()
  def extraSetup = {

      ContentHelper.createPost("Da Oostin Boyeez","MC Donalds","Hello",1,None)

  }

  var insertedPost: Post = null
  lazy val postPage = new PostPage(port)


  import postPage._

  var setupDone: Boolean = false

  var props : MongodProps = null

  def setup() = {
    if (!setupDone) {
      repo.refresh

      insertedPost = extraSetup
      setupDone = true
      UserAccountHelper.createUser("andrew", "pa$$word", "TrustedContributor")
    }


  }

  before{

    setup()

  }

  after{

  }
  "Post" must {
    "Display a post" in {
       go to post(insertedPost.id)
       title mustBe insertedPost.title
       articleText mustBe insertedPost.getCleanContent
       author mustBe insertedPost.author
    }

    "Display the biography image when viewing biographies" in {
      val bio = ContentHelper.createBiography("Da Oostin Boyeez", "leek", "assets/images/crew/otis_col.png", None)
      go to post(bio.id)
      eventually{bioImagePresent mustEqual true}
    }

    "Display post tags" in {
      val testPost = ContentHelper.createPostWithTags("Da Oostin Boyeez","Hello",ContentTypeMap("Blog"),"Da Oostin Boyeez,Jon Kevson",None)
      go to post(testPost.id)
      eventually{tagList must contain allOf("Jon Kevson","Da Oostin Boyeez")}
    }
  }



}
