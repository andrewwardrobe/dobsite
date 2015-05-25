package data

import models.ContentToTagLink


/**
 * Created by andrew on 14/05/15.
 */
trait ContentToTagFunctions {this: ContentToTagSchema =>
  import play.api.db.slick.Config.driver.simple._


  val contentTags = TableQuery[PostTagsTable]

  def link(postId: String, tagId :String)(implicit session: Session) = {
    contentTags.insert(new ContentToTagLink(postId,tagId))
  }

  def deleteLinks(implicit session: Session) = contentTags.delete
}