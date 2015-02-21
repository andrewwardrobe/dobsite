import com.daoostinboyeez.git.GitRepo
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.Logger
import play.api.test.FakeApplication
import play.api.test.Helpers._

/**
 * Created by andrew on 21/02/15.
 */
class RepoSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))

  "Repository" must {
    "Be able to list all commits" in {
      val commitList = GitRepo.find
      commitList.length must equal(6)
    }

    "Be able to show the current branch" in {
      val currBranch = GitRepo.getBranch
      currBranch must equal ("refs/heads/master")
    }

    "Be able to list all commits for a path" in {
      val commitList = GitRepo.find ("leek")
      commitList.length must equal(2)
    }
  }


   before {
    GitRepo.refresh
    val firstFile = GitRepo.createFile("Here is some data in a file")
    val secondFile = GitRepo.createFile("Some other data")

    GitRepo.newFile("leek","Here is leek")

    GitRepo.updateFile(firstFile,"Here is some data in a file I just changed")
    GitRepo.updateFile(secondFile,"Some other data, i just changed")

    GitRepo.updateFile("leek","Here is some more leeks")
  }

  after {
    GitRepo.refresh
  }

}
