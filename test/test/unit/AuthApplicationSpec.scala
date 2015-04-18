package test.unit

import models.Biography
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.{FakeApplication, FakeHeaders, FakeRequest}
import play.api.test.Helpers._
import play.api.db.DB
import test.{TestGlobal, TestConfig}
import scala.slick.jdbc.JdbcBackend._
/**
 * Created by andrew on 14/09/14.
 */
class AuthApplicationSpec extends PlaySpec with OneServerPerSuite{
  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())
  "Application" should {

    "Be able to Update a biography" in {

      database.withSession { implicit session =>
        val bio = new Biography(1, "MC Donalds", 0, "images/crew/donalds_bw.jpg", "images/crew/donalds_bw.jpg", "blah blah")
        val id = Biography.insert(bio)
        val updatedBio = new Biography(id, bio.name, bio.bioType, bio.imagePath, bio.thumbPath, "new bio text");

        val results = route(FakeRequest(POST, controllers.routes.Authorised.updateBiography().url, FakeHeaders(), updatedBio.json)).get
        status(results) mustBe (OK)
        contentType(results) mustBe Some("text/plain")
        contentAsString(results) must include (id.toString)
      }
    }


    def setup = {

    }
  }

}
