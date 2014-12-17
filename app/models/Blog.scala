package models

import java.util.Date
import org.jsoup._
import org.jsoup.safety.Whitelist
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.libs.json.{JsString, Json, JsValue, JsObject, JsNumber}

/**
 * Created by andrew on 11/10/14.
 */
case class Blog(id: Int, title: String,postType: Int, dateCreated: Date, author: String,content: String ) {

  val json: JsValue = Json.obj(
     "id" -> id,
      "title" -> title,
    "postType" -> postType,
    "dateCreated" -> dateCreated,
    "author" -> author,
   "content" -> JsString(Jsoup.clean(content,"http://localhost:9000/",Whitelist.basicWithImages()
                                                                      .preserveRelativeLinks(true)
                                                                      .addAttributes("img","class")
                                                                      .addAttributes("p","class")
                                     ))
  )


}

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

  def getXNewsItemsFromId(id: Int, max: Int)(implicit s: Session) = {
    getXItemsFromId(id,max,1)
  }
  def getXItemsFromId(id: Int, max: Int, typ: Int)(implicit s: Session) = {
    blog.filter( blogPost =>
      blogPost.id <= id &&
        blogPost.postType === typ
    ).sortBy(_.id.desc).take(5).list
  }

  def getXNewsItems(max: Int)(implicit s: Session) = {
    getXItems(1,max)
  }

  def getXItems(typ: Int,max: Int)(implicit s: Session) = {
    blog.filter( blogPost =>
      blogPost.postType === typ
    ).sortBy(_.id.desc).take(5).list
  }



  def getNews(implicit s: Session) = { blog.filter(_.postType === 1).list }

  def insert(newsItem: Blog)(implicit s: Session) = { blog.insert(newsItem) }

  def update(post :Blog)(implicit s: Session) = { blog.insertOrUpdate(post) }

  val blogForm: Form[Blog] = Form {
    mapping (
      "id" -> number,
      "title" -> text,
      "postType" -> number,
      "dateCreated" -> date,
      "author" ->text,
      "content" -> text
    )(Blog.apply)(Blog.unapply _)
  }
}
