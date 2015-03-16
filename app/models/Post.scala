package models

import java.util.Date
import com.daoostinboyeez.git.GitRepo
import org.jsoup._
import org.jsoup.safety.Whitelist
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.libs.json.{JsString, Json, JsValue, JsObject, JsNumber}

/**
 * Created by andrew on 11/10/14.
 */
case class Post(id: Int, title: String,postType: Int, dateCreated: Date, author: String,content: String ) {

  val repo = GitRepo.apply()
  def json: JsValue = Json.obj(
     "id" -> id,
      "title" -> title,
    "postType" -> postType,
    "dateCreated" -> dateCreated,
    "author" -> author,
   "content" -> JsString(Jsoup.clean(getContent(),"http://localhost:9000/",Whitelist.basicWithImages()
                                                                      .preserveRelativeLinks(true)
                                                                      .addAttributes("img","class")
                                                                      .addAttributes("p","class")
                                                                      .addAttributes("div","align")
                                     ))
  )

  def json(rev :String): JsValue = Json.obj(
    "id" -> id,
    "title" -> title,
    "postType" -> postType,
    "dateCreated" -> dateCreated,
    "author" -> author,
    "content" -> JsString(Jsoup.clean(getContent(rev),"http://localhost:9000/",Whitelist.basicWithImages()
      .preserveRelativeLinks(true)
      .addAttributes("img","class")
      .addAttributes("p","class")
      .addAttributes("div","align")
    ))
  )

  def getContent() = {
    repo.getFile(content)
  }

  def getContent(commitId :String) = {
    repo.getFile(content,commitId)
  }
}

object Post{
  class PostTable(tag:Tag) extends Table[Post](tag,"posts"){

    implicit val JavaUtilDateMapper =
      MappedColumnType .base[java.util.Date, java.sql.Timestamp] (
        d => new java.sql.Timestamp(d.getTime),
        d => new java.util.Date(d.getTime)
      )

      def id = column[Int]("ID",O.PrimaryKey,O.AutoInc)
      def title = column[String]("ITEM_TITLE")
      def postType = column[Int]("TYPE")
      def dateCreated = column[Date]("DATE_CREATED")
      def author = column[String]("AUTHOR")
      def content = column[String]("CONTENT")

      def * = (id,title,postType,dateCreated,author,content) <> ((Post.apply _).tupled, Post.unapply)
  }

  val posts = TableQuery[PostTable]

  def get(implicit s: Session) = { posts.list }
  def getById(id: Int)(implicit s: Session) = { posts.filter(_.id === id).list }
  def getByType(typ: Int)(implicit s: Session) = { posts.filter(_.postType === typ).list }
  def getXNewsItemsFromId(id: Int, max: Int)(implicit s: Session) = {
    getXItemsFromId(id,max,1)
  }

  def getXItemsFromId(id: Int, max: Int, typ: Int)(implicit s: Session) = {
    posts.filter( blogPost =>
      blogPost.id <= id &&
        blogPost.postType === typ
    ).sortBy(_.id.desc).take(5).list
  }



  def getXNewsItems(max: Int)(implicit s: Session) = {
    getXItems(1,max)
  }

  def getXItems(typ: Int,max: Int)(implicit s: Session) = {
    posts.filter( blogPost =>
      blogPost.postType === typ
    ).sortBy(_.id.desc).take(5).list
  }



  def getNews(implicit s: Session) = { posts.filter(_.postType === 1).list }

  def insert(newsItem: Post)(implicit s: Session) = { posts returning posts.map(_.id) += newsItem }

  def update(post :Post)(implicit s: Session) = { posts.insertOrUpdate(post) }


  val blogForm: Form[Post] = Form {
    mapping (
      "id" -> number,
      "title" -> text,
      "postType" -> number,
      "dateCreated" -> date,
      "author" ->text,
      "content" -> text
    )(Post.apply)(Post.unapply _)
  }
}
