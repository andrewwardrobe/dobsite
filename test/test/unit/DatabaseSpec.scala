package test.unit

import com.daoostinboyeez.git.GitRepo
import models.{Discography, Post}
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.Logger
import play.api.db.DB
import play.api.test.FakeApplication
import play.api.test.Helpers._
import test.helpers.PostHelper

import scala.slick.jdbc.JdbcBackend._
import test._
class DatabaseSpec extends PlaySpec with OneServerPerSuite{

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())

  "Database" must {
    "Be able to Insert Discography Releases" in {
      database.withSession { implicit session =>
        val disc = Discography(1, "Da Oostin Boyeez", 0, "images/dob.jpg")
        Discography.insert(disc)
        val res = Discography.get
        res.head mustEqual disc
      }
    }

    "Be able to insert and retrieve posts items" in {
      database.withSession { implicit session =>
        GitRepo.refresh
        val newsItem = PostHelper.createPost("DOB Test News Post","MC Donalds","News Content for db spec",1)
        val nonNewsItem =  PostHelper.createPost( "DOB Test Music Post","MC Donalds","Some cool DoB Music for dbspec",2)
        val result = Post.get
        result.head mustEqual newsItem
      }
    }
  }
}