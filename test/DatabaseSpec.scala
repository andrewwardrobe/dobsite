import java.util.Date

import models.{Blog, Discography}
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.db.DB

import scala.slick.jdbc.JdbcBackend._

class DatabaseSpec extends PlaySpec with OneServerPerSuite{

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
        val newsItem = Blog(1, "DOB Test News Post",1,new Date(),"MC Donalds","Some Example content blah blah blah")
        val nonNewsItem =  Blog(1, "DOB Test Music Post",2,new Date(),"MC Donalds","Some cool DoB Music")
        Blog.insert(newsItem)
        Blog.insert(nonNewsItem)
        val result = Blog.get
        result.head mustEqual newsItem
      }
    }
  }
}