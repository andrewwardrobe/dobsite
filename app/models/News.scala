package models

import java.util.Date
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
/**
 * Created by andrew on 11/10/14.
 */
case class News(id: Int, title: String, dateCreated: Date, author: String,content: String )

object News{
  class NewsTable(tag:Tag) extends Table[News](tag,"news"){

    implicit val JavaUtilDateMapper =
      MappedColumnType .base[java.util.Date, java.sql.Timestamp] (
        d => new java.sql.Timestamp(d.getTime),
        d => new java.util.Date(d.getTime))

      def id = column[Int]("ID",O.PrimaryKey,O.AutoInc)
      def title = column[String]("ITEM_TITLE")
      def dateCreated = column[Date]("DATE_CREATED")
      def author = column[String]("AUTHOR")
      def content = column[String]("CONTENT")

      def * = (id,title,dateCreated,author,content) <> ((News.apply _).tupled,News.unapply)

  }

  val news = TableQuery[NewsTable]

  def get(implicit s: Session) = { news.list }
  def getById(id: Int)(implicit s: Session) = { news.filter(_.id === id).list }

  def insert(newsItem: News)(implicit s: Session) = { news.insert(newsItem) }

}
