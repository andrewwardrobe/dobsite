import java.util.Date

import models.{News, Discography}
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

    "Be able to insert and retrieve news items" in {
      database.withSession { implicit session =>
        val newsItem = News(1, "DOB Test News Post",new Date(),"MC Donalds","Some Example content blah blah blah")
        News.insert(newsItem)
        val result = News.get
        result.head mustEqual newsItem
      }
    }
  }
}