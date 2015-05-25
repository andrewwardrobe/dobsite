package data

import java.util.Date

import models.ContentPost


/**
 * Created by andrew on 14/05/15.
 */
trait ContentPostSchema {
  import play.api.db.slick.Config.driver.simple._

  implicit val JavaUtilDateMapper =
    MappedColumnType .base[java.util.Date, java.sql.Timestamp] (
      d => new java.sql.Timestamp(d.getTime),
      d => new java.util.Date(d.getTime)
    )
  class PostTable(tag:Tag) extends Table[ContentPost](tag,"posts"){



    def id = column[String]("ID",O.PrimaryKey)
    def title = column[String]("ITEM_TITLE")
    def postType = column[Int]("TYPE")
    def dateCreated = column[Date]("DATE_CREATED")
    def author = column[String]("AUTHOR")
    def content = column[String]("CONTENT")
    def extraData = column[String]("EXTRA_DATA")
    def isDraft = column[Boolean]("DRAFT")
    def userId = column[Option[Int]]("USER_ID",O.Nullable)
    def tags = Tags.contentTags.filter(_.postId === id).flatMap(_.tagsFK)

    def * = (id,title,postType,dateCreated,author,content,extraData,isDraft,userId) <> ((ContentPost.apply _).tupled, ContentPost.unapply _)
  }
}
