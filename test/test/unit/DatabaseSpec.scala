package test.unit

import com.daoostinboyeez.git.GitRepo
import data.Content
import models.{ ContentPost}
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.Logger

import play.api.test.FakeApplication
import play.api.test.Helpers._
import test.helpers.PostHelper
import play.api.db.DB
import scala.slick.jdbc.JdbcBackend._
import test._
class DatabaseSpec extends PlaySpec with OneServerPerSuite{

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())

  "Database" must {


    "Be able to insert and retrieve posts items" in {
      database.withSession { implicit session =>
        GitRepo.refresh
        val newsItem = PostHelper.createPost("DOB Test News Post","MC Donalds","News Content for db spec",1)
        val nonNewsItem =  PostHelper.createPost( "DOB Test Music Post","MC Donalds","Some cool DoB Music for dbspec",2)
        val result = Content.get
        result.head mustEqual newsItem
      }
    }


  }


}