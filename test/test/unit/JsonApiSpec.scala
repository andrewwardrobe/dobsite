package test.unit

import controllers.routes
import models.{Post, ContentTypeMap}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.{FakeApplication, FakeRequest}
import play.api.test.Helpers._
import test.helpers.{ReactiveMongoApp, UserAccountHelper, ContentHelper}
import test.{EmbedMongoGlobal, TestGlobal, TestConfig}

/**
 * Created by andrew on 14/09/14.
 */


class JsonApiSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter with ReactiveMongoApp {

    import scala.concurrent.duration.DurationInt


    implicit override lazy val app = buildAppEmbed
    var post: Post = _

   before {
    post = ContentHelper.createPost("Leek","andrew","jimmy",ContentTypeMap.get("Blog"),None)
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

    "Provide ability to get drafts by user" in {
      val user = UserAccountHelper.createUserAndProfile("TestUser","TestUser","Contributor")
      ContentHelper.createPost("Bio 3","andrew","Bio 1",ContentTypeMap.get("Biography"),Some(user._id))
      val result = route(FakeRequest(GET, routes.JsonApi.getDraftsByUserLatestFirst(user.id).url)).get
      status(result) mustBe (OK)
    }

  }

}
