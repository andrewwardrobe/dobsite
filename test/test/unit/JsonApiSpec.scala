package test.unit

import controllers.routes
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.{FakeApplication, FakeRequest}
import play.api.test.Helpers._
import test.helpers.PostHelper
import test.{TestGlobal, TestConfig}

/**
 * Created by andrew on 14/09/14.
 */
class JsonApiSpec extends PlaySpec with OneServerPerSuite{

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  "Json API" should {

    "Be able to get content items by id" in {
      val post = PostHelper.createPost("Leek","andrew","jimmy",1)
      val result = route(FakeRequest(GET, routes.JsonApi.getPostById(post.id).url)).get
      status(result) mustBe (OK)
      contentType(result) mustBe Some("application/json")
      contentAsString(result) must include (post.id)
    }

    "Be able to get content items by title" in {
      val post = PostHelper.createPost("Leek 3","andrew","jimmey",1)
      val result = route(FakeRequest(GET, routes.JsonApi.getPostById(post.title).url)).get
      status(result) mustBe (OK)
      contentType(result) mustBe Some("application/json")
      contentAsString(result) must include (post.id)
    }

    def setup = {

    }
  }

}
