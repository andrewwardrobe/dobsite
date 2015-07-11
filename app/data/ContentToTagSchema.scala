package data

import models.{ContentTag, ContentToTagLink, ContentPost}

/**
 * Created by andrew on 14/05/15.
 */
trait ContentToTagSchema{
  import play.api.db.slick.Config.driver.simple._

  class PostTagsTable(tag: Tag) extends Table[ContentToTagLink](tag, "POST_TAGS") {
    //Pri Key
    def postId = column[String]("postId")
    def tagId = column[String]("tagId")
    def * = (postId, tagId) <> ((ContentToTagLink.apply _).tupled, ContentToTagLink.unapply)

    def postFK = foreignKey("post_fk", postId,Content.postTable)(post => post.id)
    def tagsFK = foreignKey("tag_fk", tagId, Tags.tagsTable)(tags => tags.id)
  }

  val contentTags = TableQuery[PostTagsTable]
}
