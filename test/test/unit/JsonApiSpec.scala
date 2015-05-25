package test.unit

import controllers.routes
import models.{ContentPost, ContentTypeMap}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.{FakeApplication, FakeRequest}
import play.api.test.Helpers._
import test.helpers.ContentHelper
import test.{TestGlobal, TestConfig}

/**
 * Created by andrew on 14/09/14.
 */
class JsonApiSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter{



  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  var post: ContentPost = null

   before {
    post = ContentHelper.createPost("Leek","andrew","jimmy",ContentTypeMap.get("News"),None)
    ContentHelper.createPost("Bio 1","andrew","Bio 1",ContentTypeMap.get("Biography"),None)
    ContentHelper.createPost("Bio 2","andrew","Bio 1",ContentTypeMap.get("Biography"),None)
    ContentHelper.createPost("Bio 3","andrew","Bio 1",ContentTypeMap.get("Biography"),None)
    ContentHelper.createPost("Music 1","andrew","Bio 1",ContentTypeMap.get("Music"),None)
  }

  after {
    ContentHelper.clearAll
  }

  "Json API" should {

    "Be able to get content items by id" in {

      val result = route(FakeRequest(GET, routes.JsonApi.getPostById(post.id).url)).get
      status(result) mustBe (OK)
      contentType(result) mustBe Some("application/json")
      contentAsString(result) must include (post.id)
    }

    "Be able to get content items by title" in {

      val result = route(FakeRequest(GET, routes.JsonApi.getPostById(post.title).url)).get
      status(result) mustBe (OK)
      contentType(result) mustBe Some("application/json")
      contentAsString(result) must include (post.id)
    }

    "Be able to get postItems by type" in {

    }

  }

}
