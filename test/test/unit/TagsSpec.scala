package test.unit

import data.Tags
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.db.DB
import play.api.test.FakeApplication
import play.api.test.Helpers._
import test.{TestGlobal, TestConfig}

import scala.slick.jdbc.JdbcBackend._

/**
 * Created by andrew on 09/05/15.
 */
class TagsSpec  extends PlaySpec with OneServerPerSuite with BeforeAndAfter {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))


  def database = Database.forDataSource(DB.getDataSource())
  "Tags" must {


    "Be able to insert create tags and retreive tags" in {
      database.withSession { implicit session =>
        val tag = Tags.create("Tag")
        val retrievedTag = Tags.get(tag.id)
        retrievedTag mustEqual tag
      }
    }
  }
}

