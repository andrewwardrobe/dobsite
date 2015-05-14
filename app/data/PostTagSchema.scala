package data

import models.{ContentTag, PostToTagLink, Post}

/**
 * Created by andrew on 14/05/15.
 */
trait PostToTagSchema{
  import play.api.db.slick.Config.driver.simple._

  class PostTagsTable(tag: Tag) extends Table[PostToTagLink](tag, "POST_TAGS") {
    //Pri Key
    def postId = column[String]("postId")
    def tagId = column[String]("tagId")
    def * = (postId, tagId) <> ((PostToTagLink.apply _).tupled, PostToTagLink.unapply)
    //TODO: foreign keys
    def postFK = foreignKey("post_fk", postId,Posts.postTable)(post => post.id)
    def tagsFK = foreignKey("tag_fk", tagId, Tags.tagsTable)(tags => tags.id)
  }

}
