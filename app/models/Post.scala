package models

import java.util.{UUID, Date}
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
case class Post(id: String, title: String, postType: Int, dateCreated: Date, author: String, content: String, extraData: String) {

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
                                     )),
    "extraData" -> extraData
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
    )),
    "extraData" -> extraData
  )

  def getContent() = {
    repo.getFile(content)
  }

  def getContent(commitId :String) = {
    repo.getFile(content,commitId)
  }
}

object Post{
  implicit val JavaUtilDateMapper =
    MappedColumnType .base[java.util.Date, java.sql.Timestamp] (
      d => new java.sql.Timestamp(d.getTime),
      d => new java.util.Date(d.getTime)
  )

  class PostTable(tag:Tag) extends Table[Post](tag,"posts"){



      def id = column[String]("ID",O.PrimaryKey)
      def title = column[String]("ITEM_TITLE")
      def postType = column[Int]("TYPE")
      def dateCreated = column[Date]("DATE_CREATED")
      def author = column[String]("AUTHOR")
      def content = column[String]("CONTENT")
      def extraData = column[String]("EXTRA_DATA")

      def * = (id,title,postType,dateCreated,author,content,extraData) <> ((Post.apply _).tupled, Post.unapply)
  }

  val posts = TableQuery[PostTable]

  def get(implicit s: Session) = { posts.list }
  def getById(id: String)(implicit s: Session) = { posts.filter(_.id === id).list }
  def getByTitle(title: String)(implicit s: Session) = { posts.filter(_.title.toLowerCase === title.toLowerCase).list }
  def getByType(typ: Int)(implicit s: Session) = { posts.filter(_.postType === typ).list }

  def getXNewsItemsFromId(id: String, max: Int)(implicit s: Session) = {
    getXItemsFromId(id,max,1)
  }


  def getXItemsFromId(id: String, max: Int, typ: Int)(implicit s: Session) = {
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
    ).sortBy(_.id.desc).take(max).list
  }



  def getNews(implicit s: Session) = { posts.filter(_.postType === 1).list }

  def insert(newsItem: Post)(implicit s: Session) = {
    posts.insert(newsItem)
    newsItem.id
  }

  def getByDate(implicit  s: Session) = { posts.sortBy(_.dateCreated.desc).list}

  def getByDate(typ: Int)(implicit  s: Session) = { posts.filter(_.postType === typ).sortBy(_.dateCreated.desc).list}

  def getByDate(typ: Int, max :Int)(implicit  s: Session) = { posts.filter(_.postType === typ).sortBy(_.dateCreated.desc).take(max).list}

  def getByDate(beforeDate: Date)(implicit  s: Session) = {
    posts.filter(_.dateCreated < beforeDate).sortBy(_.dateCreated.desc).list
  }

  def getByDate(beforeDate: Date, max :Int)(implicit  s: Session) = {
    posts.filter(_.dateCreated < beforeDate).sortBy(_.dateCreated.desc).take(max).list
  }



  def getByDate(typ: Int,beforeDate: Date )(implicit  s: Session) = {
    posts.filter( post =>
      post.dateCreated < beforeDate
      && post.postType === typ
    ).sortBy(_.dateCreated.desc).list
  }

  def getByDate(typ: Int,beforeDate: Date,max :Int )(implicit  s: Session) = {
    posts.filter( post =>
      post.dateCreated < beforeDate
        && post.postType === typ
    ).sortBy(_.dateCreated.desc).take(max).list
  }


  def update(post :Post)(implicit s: Session) = { posts.insertOrUpdate(post) }

  def extraDataToJson(extraData:String)  = {
    val str : StringBuilder = new StringBuilder
      str.append("{")
    extraData.split("\n").foreach{
      s =>
        val parts = s.split("=")
        if(parts.length > 1) {
          str.append("\"" + parts(0) + "\":"+"\""+parts(1)+"\",")
        }
    }
    str.append("}")
    str.toString().replace(",}","}")
  }
  val blogForm: Form[Post] = Form {
    mapping (
      "id" -> text,
      "title" -> text,
      "postType" -> number,
      "dateCreated" -> date,
      "author" ->text,
      "content" -> text,
      "extraData" -> text
    )(Post.apply)(Post.unapply _)
  }
}
