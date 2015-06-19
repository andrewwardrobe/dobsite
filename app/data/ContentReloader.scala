package data

import com.daoostinboyeez.git.GitRepo
import models.ContentMeta
import play.api.Logger
import play.api.db.DB
import play.api.Play.current

import scala.slick.jdbc.JdbcBackend._

/**
 * Created by andrew on 19/06/15.
 */
class ContentReloader(repo: GitRepo){

  private def database = Database.forDataSource(DB.getDataSource())


  def reload = {
    val fileList = repo.lsFiles
    fileList.foreach { file =>
      loadFromCommitMsg(file)
    }
  }

  def loadFromCommitMsg(file: String) : Unit = {
    val commitMsg = repo.getLastCommitMsg(file)
    val post = ContentMeta.toPost(commitMsg)
      if(post != null) {
        database.withSession { implicit session =>
          Content.insert(post)
        }
      }
  }
}
