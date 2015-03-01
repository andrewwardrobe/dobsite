import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter}
import org.scalatestplus.play.{FirefoxFactory, OneBrowserPerSuite, OneServerPerSuite, PlaySpec}
import play.api.db.DB
import play.api.test.{FakeRequest, FakeApplication}
import play.api.test.Helpers._
import jp.t2v.lab.play2.auth.test.Helpers._

import scala.slick.jdbc.JdbcBackend._

/**
 * Created by andrew on 01/03/15.
 */
class PostEditorSpec  extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with FirefoxFactory with BeforeAndAfter with BeforeAndAfterAll  {
  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  def database = Database.forDataSource(DB.getDataSource())

  beforeAll{
    val signUp = new SignUpPage(port)
    signUp.signup("andrew","andrew@dob.com","pa$$word")
  }

  after{
    
  }
  
  "Post Editor" must {

    "Display a List of Revisions" in {

    }
  }
}
