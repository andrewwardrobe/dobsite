package models


import data.{PostToTagSchema, PostToTagFunctions}
import models._
import play.api._
import play.api.db.slick._


case class ContentTag(id:String,title: String)

trait ContentTagSchema {

  import play.api.db.slick.Config.driver.simple._

  class PostTagTable(tag: Tag) extends Table[ContentTag](tag, "POSTTAGS") {
    //Pri Key
    def id = column[String]("id", O.PrimaryKey)

    def title = column[String]("title")

    def * = (id, title) <>((ContentTag.apply _).tupled, ContentTag.unapply)
  }

}

trait TagFunctions{ this: ContentTagSchema =>

  import play.api.db.slick.Config.driver.simple._

  import java.util.UUID

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


object ContentTag extends TagFunctions with ContentTagSchema{

}

case class PostToTagLink(postId :String, tagID :String)


