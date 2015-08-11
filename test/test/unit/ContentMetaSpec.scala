package test.unit

import java.util.{UUID, Date}

import com.daoostinboyeez.git.GitRepo
import com.daoostinboyeez.site.exceptions.InvalidMetaJsonException
import models.{Post, ContentMeta}
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.test.FakeApplication
import play.api.test.Helpers._
import reactivemongo.bson.BSONObjectID
import test._
import test.helpers.ContentHelper

/**
 * Created by andrew on 21/02/15.
 */
class ContentMetaSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo ++ TestConfig.withEmbbededMongo, withGlobal = Some(EmbedMongoGlobal))

  
  "Content Meta Spec" must {

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

      var meta = ContentMeta.getMeta(commitMgs)

      meta must equal("""{ "field":"value" }""")

    }


    "Be able to serialize json string into a post" in  {
      val post = new Post(BSONObjectID.generate, "title", 2, new Date(), "Andrew", "12345678", """{ "thumb":"assets/leek.jpg"}""",false, None,None)

      val metaString = ContentMeta.toMeta(post)
      val createdPost = ContentMeta.toPost(metaString)

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
        val createdPost = ContentMeta.toPost(metaString)
      }
    }
  }

  "Be able to make a commit message with meta data" in {
    val post = new Post(BSONObjectID.generate, "title", 2, new Date(), "Andrew", "12345678", """{ "thumb":"assets/leek.jpg"}""",false, None,None)
    val commitMsg = "My Commit Msg"
    val expected = commitMsg + "\n\n" + ContentMeta.toMeta(post)

    val actual = ContentMeta.makeCommitMsg(commitMsg,post)

    actual mustEqual expected

  }



}
