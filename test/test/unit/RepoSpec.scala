package test.unit

import com.daoostinboyeez.git.GitRepo
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.FakeApplication
import play.api.test.Helpers._
import test._

/**
 * Created by andrew on 21/02/15.
 */
class RepoSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))
  val repo = GitRepo.apply()
  
  "Content Repository" must {
    "Be able to show the current branch" in {
      val currBranch = repo.getBranch
      currBranch must equal ("refs/heads/master")
    }

    "Be able to list all commits" in {
      val commitList = repo.find
      commitList.length must equal(7) //The setup does a commit so this should be 7
    }

    "Be able to list all commits for a path" in {
      val commitList = repo.find ("leek")
      commitList.length must equal(2)
    }

    "Be able to list all commit dates" in {
      val commitList = repo.find
      commitList.length must equal(7) //The setup does a commit so this should be 7
    }

    "Be able to list all commit dates for a path" in {
      val commitList = repo.findRevDates ("leek")
      commitList.length must equal(2)
    }



    "Be able to list all commits along side its commit date" in {
      val commitList = repo.findWithDate
      commitList.length must equal(7) //The setup does a commit so this should be 7
    }

    "Be able to list all commits along side its commit date for a path" in {
      val commitList = repo.findWithDate ("leek")
      commitList.length must equal(2)
    }


  }


   before {
    repo.refresh
    val firstFile = repo.createFile("Here is some data in a file")
    val secondFile = repo.createFile("Some other data")

    repo.newFile("leek","Here is leek")

    repo.updateFile(firstFile,"Here is some data in a file I just changed")
    repo.updateFile(secondFile,"Some other data, i just changed")

    repo.updateFile("leek","Here is some more leeks")
  }

  after {
    repo.refresh
  }

}
