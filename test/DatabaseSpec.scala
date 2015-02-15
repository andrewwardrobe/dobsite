import java.util.Date

import models.{Blog, Discography}
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.db.DB
import play.api.test.FakeApplication
import play.api.test.Helpers._

import scala.slick.jdbc.JdbcBackend._

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

    "Be able to insert and retrieve blog items" in {
      database.withSession { implicit session =>
        val newsItem = PostHelper.createPost("DOB Test News Post","MC Donalds","Some Example content blah blah blah",1)
        val nonNewsItem =  PostHelper.createPost( "DOB Test Music Post","MC Donalds","Some cool DoB Music",2)


        val result = Blog.get
        result.head mustEqual newsItem
      }
    }
  }
}