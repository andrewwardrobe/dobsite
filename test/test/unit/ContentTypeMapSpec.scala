package test.unit

import java.text.{SimpleDateFormat, DateFormat}

import com.daoostinboyeez.git.GitRepo
import models.{ContentTypeMap}
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}

import play.api.test.FakeApplication
import play.api.test.Helpers._
import test._
import test.helpers.ContentHelper



class ContentTypeMapSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo
    ++ withPostTypeMapConfig, withGlobal = Some(TestGlobal))

  def withPostTypeMapConfig: Map[String,String] = {
    Map("contenttypes.map"->"1 -> Blog,2 -> Music,3 -> Gaz Three,4 -> Biography" )
  }

  "Content type Map" must {
    "Be able to map a post type id to its name" in {
      val postTypeName = ContentTypeMap(1)
      postTypeName mustEqual("Blog")
    }

    "Be able to map a post type name to its id" in {
      val postTypeId = ContentTypeMap("Blog")
      postTypeId mustEqual(1)
    }
  }

}
