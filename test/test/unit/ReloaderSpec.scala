package test.unit

import com.daoostinboyeez.git.GitRepo
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.db.DB
import play.api.test.FakeApplication
import play.api.test.Helpers._
import test._
import test.helpers.ContentHelper
import data.{ContentReloader, Content}

import scala.slick.jdbc.JdbcBackend._

/**
 * Created by andrew on 21/02/15.
 */
class ReloaderSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  lazy val repo = GitRepo.apply()
  def database = Database.forDataSource(DB.getDataSource())
  "Reloader" must {

    "Reload Post from the Repo in to the DB" in {
      val reloader = new ContentReloader(repo)
      reloader.reload
      database.withSession { implicit session =>
        Content.get.length must equal(2)
      }
    }

    before {
      database.withSession { implicit session =>
        repo.refresh
        ContentHelper.createPost("Post 1", "MC Donalds", "Some Example Content", 1, "", None)
        ContentHelper.createPost("Post 2", "MC Donalds", "Some Example Content", 1, "", None)
        Content.deleteAll
      }
    }
  }




}
