package data

import com.daoostinboyeez.git.GitRepo
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
    insert(post).map { res =>
      repo.newFile(post._id.stringify, post.content, ContentMeta.makeCommitMsg("Created", post))
    }
  }

  def save(post: MongoPost, repo: GitRepo) = {
    update(post._id.stringify,post).map { res =>
      repo.updateFile(post._id.stringify, post.content, ContentMeta.makeCommitMsg("Updated", post))
    }
  }
}
