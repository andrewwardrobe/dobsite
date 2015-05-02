package test.unit

import models.UserRole
import models.UserRole.NormalUser
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.FakeApplication
import play.api.test.Helpers._
import test._

/**
 * Created by andrew on 21/02/15.
 */
class UserAccountSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))

  
  "User Account" must {

      "Have associated user profiles" in pending
      "Allow for user aliases" in pending
      "Have a default alias" in pending
  }




}
