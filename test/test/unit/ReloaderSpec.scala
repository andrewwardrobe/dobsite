package test.unit

import com.daoostinboyeez.git.GitRepo
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.db.DB
import play.api.test.FakeApplication
import play.api.test.Helpers._
import test._
import test.helpers.ContentHelper
import data.{Users, Profiles, ContentReloader, Content}

import scala.concurrent.Await
import scala.slick.jdbc.JdbcBackend._

/**
 * Created by andrew on 21/02/15.
 */
class ReloaderSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter with ScalaFutures {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo ++ TestConfig.withEmbbededMongo, withGlobal = Some(EmbedMongoGlobal))
  lazy val repo = GitRepo.apply()

  "Reloader" must {

    "Reload Post from the Repo in to the DB" in {
      val reloader = new ContentReloader(repo)
      reloader.reload

        Content.count.futureValue must equal(2)

    }

    before {
      import scala.concurrent.duration.DurationInt
        repo.refresh
        ContentHelper.createPost("Post 1", "MC Donalds", "Some Example Content", 1, "", None)
        ContentHelper.createPost("Post 2", "MC Donalds", "Some Example Content", 1, "", None)
      Await.ready(Content.deleteAll,10 seconds)


    }
  }




}
