package data

import com.daoostinboyeez.git.GitRepo
import com.daoostinboyeez.site.exceptions.{PostUpdateException, PostInsertException}
import models.{ContentMeta, Post}
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * Created by andrew on 14/05/15.
 */
object Content extends DAOBase[Post]("posts"){

  import models.JsonFormats._
  def findByType(typeId: Int) = {
    find(Json.obj("postType" -> typeId ))
  }


  def create(post : Post, repo :GitRepo) = {
    Logger.info("create" +post.id)

    insert(post).map { res =>
      res.ok match {
        case true =>
          repo.newFile(post._id.stringify, post.content, ContentMeta.makeCommitMsg("Created", post))
          post
        case false =>
          throw new PostInsertException(res.errmsg.getOrElse(""))
      }

    }
  }

  def save(post: Post, repo: GitRepo) = {

    update(post._id.stringify,post).map { res =>
      res.ok match {
        case true =>
          repo.updateFile (post._id.stringify, post.content, ContentMeta.makeCommitMsg ("Updated", post) )
          post
        case false =>
          throw new PostUpdateException(res.errmsg.getOrElse(""))
      }
    }
  }
}
