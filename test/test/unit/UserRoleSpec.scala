package test.unit

import com.daoostinboyeez.git.GitRepo
import models.UserRole
import models.UserRole.NormalUser
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.FakeApplication
import play.api.test.Helpers._
import test._
import test.helpers.ReactiveMongoApp

/**
 * Created by andrew on 21/02/15.
 */
class UserRoleSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter with ReactiveMongoApp {

  import scala.concurrent.duration.DurationInt


  implicit override lazy val app = buildApp


  "User Role" must {

      "Be able to load primary roles" in {
        val role = UserRole.valueOf("TrustedContributor")
        role.roles must contain ("Biography")
      }

    "Be able to load inheirited roles" in {
      val role = UserRole.valueOf("TrustedContributor")
      role.roles must contain ("Blog")
    }

    "Be able to load indirectly inhierited roles" in {
      val role = UserRole.valueOf("TrustedContributor")
      role.roles must contain ("Blog")
    }

    "Confirm a permission" in {
      val role = UserRole.valueOf("TrustedContributor")
      role.hasPermission("Biography") mustEqual true
    }

    "Confirm lack of a permission" in {
      val role = UserRole.valueOf("TrustedContributor")
      role.hasPermission("nonexistantpermission") mustEqual false
    }

    "Be able to load primary authorities" in {
      val role = UserRole.valueOf("TrustedContributor")
      role.authorities must contain ("Contributor")
    }

    "include itself in the list of primary authorities" in {
      val role = UserRole.valueOf("TrustedContributor")
      role.authorities must contain ("TrustedContributor")
    }

    "Be able to load directly inheirited authorities" in {
      val role = UserRole.valueOf("TrustedContributor")
      role.authorities must contain ("NormalUser")
    }

    "Leek" in {
      UserRole.roleHasAuthority(UserRole.valueOf("TrustedContributor"),NormalUser) mustEqual true
    }


  }




}
