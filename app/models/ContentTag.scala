package models

import java.util.UUID

import models._
import play.api._
import play.api.db.slick._


case class ContentTag(id:String,title: String)

object ContentTag {

  import play.api.db.slick.Config.driver.simple._
  class PostTagTable(tag: Tag) extends Table[ContentTag](tag, "POSTTAGS") {
    //Pri Key
    def id = column[String]("id",O.PrimaryKey)
    def title = column[String]("title")
    def * = (id, title) <> ((ContentTag.apply _).tupled, ContentTag.unapply)
  }

  val tagsTable = TableQuery[PostTagTable]

  def create(title:String)(implicit session: Session) = {
    val tag = new ContentTag(UUID.randomUUID.toString,title)
    tagsTable.insert(tag)
    tag
  }

  def get(id:String)(implicit session: Session) = {
    tagsTable.filter(_.id === id).list.head
  }

  def getAll (implicit session: Session) = {
    tagsTable.list
  }
}

case class PostToTag(postId :String, tagID :String)

object PostToTag {
  import play.api.db.slick.Config.driver.simple._

  class PostTagsTable(tag: Tag) extends Table[PostToTag](tag, "POST_TAGS") {
    //Pri Key
    def postId = column[String]("postId")
    def tagId = column[String]("tagId")
    def * = (postId, tagId) <> ((PostToTag.apply _).tupled, PostToTag.unapply)
    //TODO: foreign keys
    def postFK = foreignKey("post_fk", postId,Post.posts)(post => post.id)
    def tagsFK = foreignKey("tag_fk", tagId, ContentTag.tagsTable)(tags => tags.id)
  }

  val postTags = TableQuery[PostTagsTable]

  def link(postId: String, tagId :String)(implicit session: Session) = {
    postTags.insert(new PostToTag(postId,tagId))
  }

  def getAll (implicit session: Session) = {
    postTags.list
  }
}
