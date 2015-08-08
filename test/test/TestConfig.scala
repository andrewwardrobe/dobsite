package test

import java.io.File

/**
 * Created by andrew on 15/02/15.
 */
object TestConfig {
  def withTempGitRepo = {
    val temp = File.createTempFile("TestGitRepo","")
    temp.delete
    val path = temp.getAbsolutePath  +"/.git"
    Map("git.repo.dir" -> path, "git.repo.testmode" -> true)
  }

  def withTestSecurityRoles = {
    Map(
      "userrole.TrustedContributor.roles" -> "bios",
      "userrole.TrustedContributor.auhtorities" -> "Contributor",
      "userrole.Contributor.roles" -> "news,music",
      "userrole.Contributor.auhtorities" -> "NormalUser",
      "userrole.NormalUser.roles" -> "leek",
      "userrole.NormalUser.auhtorities" -> ""

    )

  }

  def withEmbbededMongo = {
    Map("mongodb.uri" -> "mongodb://localhost:12345")
  }
}
