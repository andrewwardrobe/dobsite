package models

import java.util.{UUID, Date}
import com.daoostinboyeez.git.GitRepo
import dal.PostToTagDAO
import org.jsoup._
import org.jsoup.safety.Whitelist
import play.api.{Play, Logger}
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.libs.json.{JsString, Json, JsValue, JsObject, JsNumber}

import scala.collection.mutable.ListBuffer

/**
 * Created by andrew on 11/10/14.
 */
case class Post(id: String, title: String, postType: Int, dateCreated: Date, author: String, content: String, extraData: String,isDraft: Boolean) {

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
    "extraData" -> extraData,
    "isDraft" -> isDraft
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
    "extraData" -> extraData,
    "isDraft" -> isDraft
  )

  def getContent() = {
    repo.getFile(content)
  }

  def getContent(commitId :String) = {
    repo.getFile(content,commitId)
  }

  def tags(implicit s: Session) = {
    val lb = new ListBuffer[String]
    Post.getTags(id).foreach { t =>
      lb += t.title
    }
    lb.toList
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
      def isDraft = column[Boolean]("DRAFT")
      def tags = PostToTagDAO.postTags.filter(_.postId === id).flatMap(_.tagsFK)

      def * = (id,title,postType,dateCreated,author,content,extraData,isDraft) <> ((Post.apply _).tupled, Post.unapply _)
  }

  val posts = TableQuery[PostTable]


  def get(implicit s: Session) = { posts.list }
  def getById(id: String)(implicit s: Session) = { posts.filter(_.id === id).list }
  def getByTitle(title: String)(implicit s: Session) = { posts.filter(_.title.toLowerCase === title.toLowerCase).list }
  def getByType(typ: Int)(implicit s: Session) = { posts.filter(_.postType === typ).list }

  def getTags(id:String)(implicit s:Session) = {
    PostToTagDAO.postTags.filter(_.postId === id).flatMap(_.tagsFK).list
  }

  def getTagsAsJson(id:String)(implicit s:Session) = {
    val lb = new ListBuffer[String]
    getTags(id).foreach { t =>
      lb += t.title
    }
    Json.toJson(lb.toList)
  }

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

  def getByDate(implicit  s: Session) = { posts.filter(post => post.isDraft === false).sortBy(_.dateCreated.desc).list}

  def getByDate(typ: Int)(implicit  s: Session) = {
    posts.filter(post => post.postType === typ && post.isDraft === false ).sortBy(_.dateCreated.desc).list
  }

  def getByDate(typ: Int, max :Int)(implicit  s: Session) = {
    posts.filter(post => post.postType === typ && post.isDraft === false).sortBy(_.dateCreated.desc).take(max).list
  }

  def getByDate(beforeDate: Date)(implicit  s: Session) = {
    posts.filter(post => post.dateCreated < beforeDate && post.isDraft === false).sortBy(_.dateCreated.desc).list
  }

  def getByDate(beforeDate: Date, max :Int)(implicit  s: Session) = {
    posts.filter(post => post.dateCreated < beforeDate && post.isDraft === false).sortBy(_.dateCreated.desc).take(max).list
  }



  def getByDate(typ: Int,beforeDate: Date )(implicit  s: Session) = {
    posts.filter( post =>
      post.dateCreated < beforeDate
      && post.postType === typ
       && post.isDraft === false
    ).sortBy(_.dateCreated.desc).list
  }

  def getByDate(typ: Int,beforeDate: Date,max :Int )(implicit  s: Session) = {
    posts.filter( post =>
      post.dateCreated < beforeDate
        && post.postType === typ
        && post.isDraft === false
    ).sortBy(_.dateCreated.desc).take(max).list
  }

  def getByDateWithDrafts(implicit  s: Session) = { posts.sortBy(_.dateCreated.desc).list}

  def getByDateWithDrafts(typ: Int)(implicit  s: Session) = {
    posts.filter(post => post.postType === typ ).sortBy(_.dateCreated.desc).list
  }

  def getByDateWithDrafts(typ: Int, max :Int)(implicit  s: Session) = {
    posts.filter(post => post.postType === typ).sortBy(_.dateCreated.desc).take(max).list
  }

  def getByDateWithDrafts(beforeDate: Date)(implicit  s: Session) = {
    posts.filter(post => post.dateCreated < beforeDate).sortBy(_.dateCreated.desc).list
  }

  def getByDateWithDrafts(beforeDate: Date, max :Int)(implicit  s: Session) = {
    posts.filter(post => post.dateCreated < beforeDate).sortBy(_.dateCreated.desc).take(max).list
  }

  def delete(id:String)(implicit  s: Session) = posts.filter(_.id === id).delete


  def getByDateWithDrafts(typ: Int,beforeDate: Date )(implicit  s: Session) = {
    posts.filter( post =>
      post.dateCreated < beforeDate
        && post.postType === typ
    ).sortBy(_.dateCreated.desc).list
  }

  def getByDateWithDrafts(typ: Int,beforeDate: Date,max :Int )(implicit  s: Session) = {
    posts.filter( post =>
      post.dateCreated < beforeDate
        && post.postType === typ
    ).sortBy(_.dateCreated.desc).take(max).list
  }

  def clearAll(implicit  s: Session) = { posts.delete }
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
      "extraData" -> text,
      "isDraft" -> boolean
    )(Post.apply)(Post.unapply _)
  }
}



import play.api.Play.current
object PostTypeMap {
  private lazy val typeMap = {
    val tmp = scala.collection.mutable.Map[Int, String]()
    Play.configuration.getString("posttypes.map").getOrElse("").split(",").map(s => s.trim).toList.foreach{ str =>
      if(str != "") {
        val parts = str.split("->").map(s => s.trim).toList
        if(parts.length == 2)
          tmp += (parts(0).toInt -> parts(1))
      }
    }
    scala.collection.immutable.Map[Int, String](tmp.toSeq:_*)
  }
  private lazy val revMap = typeMap.map(_.swap)



  def allRoles = typeMap.values.toList

  def apply(key:String) = get(key)

  def apply(key:Int) = get(key)

  def get(key:String) = revMap.getOrElse(key,-1)

  def get(key:Int) = typeMap.getOrElse(key,"")
}

