package test.unit

import java.util.{UUID, Date}

import com.daoostinboyeez.git.GitRepo
import com.daoostinboyeez.site.exceptions.InvalidMetaJsonException
import models.{Post, PostMeta}
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.test.FakeApplication
import play.api.test.Helpers._
import test._
import test.helpers.PostHelper

/**
 * Created by andrew on 21/02/15.
 */
class PostMetaSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))

  
  "Post Meta Spec" must {

    "Be able to convert a post to a meta data string " +
      "containing all fields but the content and filename" in {

    }

    "Be able to meta from a commit message" in {
      val commitMgs = """
        |Some Commit Message
        |
        |++++META++++
        |{ "field":"value" }
        |----META----
      """.stripMargin

      var meta = PostMeta.getMeta(commitMgs)

      meta must equal("""{ "field":"value" }""")

    }


    "Be able to serialize json string into a post" in  {
      val post = new Post(UUID.randomUUID().toString(), "title", 2, new Date(), "Andrew", "12345678", """{ "thumb":"assets/leek.jpg"}""")

      val metaString = PostMeta.toMeta(post)
      val createdPost = PostMeta.toPost(metaString)

      createdPost mustEqual (post)

    }

    "Throw an Exception if mata dat is invalid " in  {
      val metaString = """
                         |Some Commit Message
                         |
                         |++++META++++
                         |{ "field":"value" }
                         |----META----
                       """.stripMargin
      intercept[InvalidMetaJsonException] {
        val createdPost = PostMeta.toPost(metaString)
      }
    }
  }

  "Be able to make a commit message with meta data" in {
    val post = new Post(UUID.randomUUID().toString(), "title", 2, new Date(), "Andrew", "12345678", """{ "thumb":"assets/leek.jpg"}""")
    val commitMsg = "My Commit Msg"
    val expected = commitMsg + "\n\n" + PostMeta.toMeta(post)

    val actual = PostMeta.makeCommitMsg(commitMsg,post)

    actual mustEqual expected

  }



}