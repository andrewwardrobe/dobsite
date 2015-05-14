package data

import models.PostToTagLink

/**
 * Created by andrew on 14/05/15.
 */
trait PostToTagFunctions {this: PostToTagSchema =>
  import play.api.db.slick.Config.driver.simple._


  val postTags = TableQuery[PostTagsTable]

  def link(postId: String, tagId :String)(implicit session: Session) = {
    postTags.insert(new PostToTagLink(postId,tagId))
  }

  def getAll (implicit session: Session) = {
    postTags.list
  }
}