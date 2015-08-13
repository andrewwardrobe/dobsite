package data

import com.daoostinboyeez.git.GitRepo
import models.ContentMeta
import play.api.Logger
import play.api.db.DB
import play.api.Play.current

import scala.concurrent.Await


/**
 * Created by andrew on 19/06/15.
 */
class ContentReloader(repo: GitRepo){

  import models.JsonFormats._


  def reload = {
    val fileList = repo.lsFiles
    fileList.foreach { file =>
      loadFromCommitMsg(file)
    }
  }

  def loadFromCommitMsg(file: String) : Unit = {
    import scala.concurrent.duration.DurationInt
    val commitMsg = repo.getLastCommitMsg(file)
    val post = ContentMeta.toPost(commitMsg)
    if(post != null) {
          Await.result(Content.insert(post),10 seconds)
    }
  }
}
