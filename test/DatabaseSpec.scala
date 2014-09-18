import models.Discography
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.db.DB

import scala.slick.jdbc.JdbcBackend._

class DatabaseSpec extends PlaySpec with OneServerPerSuite{

  "Database" must {
    "Be able to Insert Discogography Releases" in {
      Database.forDataSource(DB.getDataSource()).withSession { implicit session =>
        val disc = Discography(1, "Da Oostin Boyeez", 0, "images/dob.jpg")
        Discography.insert(disc)
        val res = Discography.getById(1)
        res.head mustEqual disc
      }
    }
  }
}