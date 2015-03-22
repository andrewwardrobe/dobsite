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
}