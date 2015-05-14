package test.unit

import java.text.{SimpleDateFormat, DateFormat}

import com.daoostinboyeez.git.GitRepo
import models.{ContentTypeMap, Biography, Discography, ContentPost}
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.db.DB
import play.api.test.FakeApplication
import play.api.test.Helpers._
import test._
import test.helpers.PostHelper

import scala.slick.jdbc.JdbcBackend._

class PostTypeMapSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo
    ++ withPostTypeMapConfig, withGlobal = Some(TestGlobal))

  def withPostTypeMapConfig: Map[String,String] = {
    Map("posttypes.map"->"1 -> News,2 -> Music,3 -> Gaz Three,4 -> Biography" )
  }

  "Post type Map" must {
    "Be able to map a post type id to its name" in {
      val postTypeName = ContentTypeMap(1)
      postTypeName mustEqual("News")
    }

    "Be able to map a post type name to its id" in {
      val postTypeId = ContentTypeMap("News")
      postTypeId mustEqual(1)
    }
  }

}
