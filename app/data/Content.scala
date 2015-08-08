package data

import com.daoostinboyeez.git.GitRepo
import com.daoostinboyeez.site.exceptions.{PostUpdateException, PostInsertException}
import models.{ContentMeta, MongoPost}
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * Created by andrew on 14/05/15.
 */
object Content extends DAOBase[MongoPost]("posts"){

  import models.JsonFormats._
  def findByType(typeId: Int) = {
    find(Json.obj("typeId" -> typeId ))
  }

  //Todo write this
  def create(post : MongoPost, repo :GitRepo) = {
    insert(post.copy(extraData = post.extraDataToJson)).map { res =>
      res.ok match {
        case true =>
          repo.newFile(post._id.stringify, post.content, ContentMeta.makeCommitMsg("Created", post))
          post
        case false =>
          throw new PostInsertException(res.errmsg.getOrElse(""))
      }

    }
  }

  def save(post: MongoPost, repo: GitRepo) = {
    update(post._id.stringify,post.copy(extraData = post.extraDataToJson)).map { res =>
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
