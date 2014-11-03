package models

import java.util.Date
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
/**
 * Created by andrew on 11/10/14.
 */
case class Blog(id: Int, title: String,postType: Int, dateCreated: Date, author: String,content: String )

object Blog{
  class BlogTable(tag:Tag) extends Table[Blog](tag,"blog"){

    implicit val JavaUtilDateMapper =
      MappedColumnType .base[java.util.Date, java.sql.Timestamp] (
        d => new java.sql.Timestamp(d.getTime),
        d => new java.util.Date(d.getTime))

      def id = column[Int]("ID",O.PrimaryKey,O.AutoInc)
      def title = column[String]("ITEM_TITLE")
      def postType = column[Int]("TYPE")
      def dateCreated = column[Date]("DATE_CREATED")
      def author = column[String]("AUTHOR")
      def content = column[String]("CONTENT")

      def * = (id,title,postType,dateCreated,author,content) <> ((Blog.apply _).tupled, Blog.unapply)

  }

  val blog = TableQuery[BlogTable]

  def get(implicit s: Session) = { blog.list }
  def getById(id: Int)(implicit s: Session) = { blog.filter(_.id === id).list }
  def getNewsByRange(minId: Int, maxId: Int)(implicit s: Session) = {
     blog.filter( blogPost =>
       blogPost.id >= minId && blogPost.id <= maxId &&
       blogPost.postType === 1 ).list
  }

  def getNews(implicit s: Session) = { blog.filter(_.postType === 1).list }

  def insert(newsItem: Blog)(implicit s: Session) = { blog.insert(newsItem) }

}
