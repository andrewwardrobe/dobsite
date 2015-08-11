package controllers


import java.text.SimpleDateFormat
import java.util.Date

import _root_.data.{ContentQueries, Content}
import com.daoostinboyeez.git.GitRepo
import models._
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.json.Json._
import reactivemongo.api.QueryOpts

import scala.collection.mutable.ListBuffer
import scala.util.parsing.json.{JSONObject, JSONArray}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * Created by andrew on 07/09/14.
 */
object JsonApi extends Controller {

 import models.JsonFormats._
  //implicit val tagFormat = Json.format[ContentTag]
  implicit val commitFormat =  Json.format[ContentMeta]
  val repo = GitRepo.apply()


  def getNews = Action.async { implicit response => //Todo change name to get Blogs
    Content.findByType(ContentTypeMap("Blog")).map { articles =>
      Ok(toJson(articles))
    }
  }

  def getContentByType(contentType: Int) = Action.async { implicit response =>
    Content.findByType(contentType).map { articles =>
      Ok(toJson(articles))
    }
  }
  def getPostById(id: String) = Action.async { implicit response =>
    val regex = "^[0-9a-fA-F]{24}$".r
     id match {
      case regex() => {
        Logger.info("Post ID")
        Content.findById(id).map { posts =>
          posts.headOption match {
            case Some(post) => Ok(toJson(posts))
            case None => BadRequest("Not Found")
          }
        }
      }
      case _ => {
        Logger.info("Title")
        val title = id.replace("_"," ")
        Content.find(Json.obj("title" -> title )).map { posts =>
          posts.headOption match {
            case Some(post) => Ok(toJson(posts))
            case None => BadRequest("Not Found")
          }
        }
      }
    }

  }

  //Todo delete this function
  def getContentTags(id:String) = Action.async { implicit response =>
    Content.findById(id).map { posts =>
      posts.headOption match {
        case Some(post) => post.tags match {
          case Some(tags) => Ok(toJson(tags))
          case None => NoContent
        }
        case None => NotFound(s"Cannot Find Post with id $id")
      }
    }
  }

  def getPostRevisionById(id: String, revId : String) = Action.async { implicit response =>
    Content.findById(id).map { posts =>
      posts.headOption match {
        case Some(post) => Ok(toJson(post.revision(revId,repo)))
        case None => BadRequest("Not Found")
      }
    }
  }

  //Todo should this be a future??
  def getRevisions(id: String) =  Action { implicit response =>
    val revisions = repo.findRevDates(id)
    revisions.isEmpty match {
      case false => {
        Ok(toJson(revisions))
      }
      case true => {
        Ok(toJson("None"))
      }
    }
  }
  //Todo should this be a future??
  def getRevisionsWithDates(id: String) =  Action { implicit response =>
    val revisions = repo.findWithDate(id)
        revisions.isEmpty match {
          case false => {
            val json = ListBuffer[JsValue]()
            revisions.foreach( j => json += toJson(j))
            Ok(toJson(toJson(json)))
          }
          case true => {
            Ok(toJson("None"))
          }
        }
  }


  def getContentByDate(typ: Int)  = Action.async { implicit response =>
    val query = ContentQueries.byType(typ)
    Content.find(query,Json.obj("dateCreated" -> -1 )).map { posts =>
      Ok(toJson(posts))
    }
  }


  def getDraftsByUserLatestFirst(id:String) = Action.async { implicit response=>
    val query = Json.obj(
      "userId" -> Json.obj("$oid" -> id),
      "isDraft" -> true
    )
    Content.find(query,Json.obj("dateCreated" -> -1 )).map { posts =>
      Ok(toJson(posts))
    }
  }

  def getContentByUserLatestFirst(id:String) = Action.async { implicit response=>
    val query = Json.obj(
      "userId" -> Json.obj("$oid" -> id),
      "isDraft" -> false
    )
    Content.find(query,Json.obj("dateCreated" -> -1 )).map { posts =>
      Ok(toJson(posts))
    }
  }

  def getRandomPosts(typ:Int, max : Int) = Action.async { implicit response =>
    Content.findByType(typ).map { articles =>
      Ok(toJson(articles))
    }
  }

  def getContentByDateStart(typ: Int,startDate: String,max :Int)  = Action.async { implicit response =>
    val date = startDate match {
      case s if s.isEmpty() || s == "" => {
        new Date()
      }
      case s if s == "today" => {
        new Date()
      }
      case _ => {
        val df = new SimpleDateFormat("yyyyMMddHHmmss")
        df.parse(startDate)
      }
    }
    Content.find(ContentQueries.liveContentByTypeLatestFirst(typ,date), max).map{ posts =>
      Ok(toJson(posts))
    }
  }

  def getContentByAuthorDateStart(author: String, typ: Int, startDate: String, max: Int) = Action.async { implicit response =>
    val date = startDate match {
      case s if s.isEmpty() || s == "" => {
        new Date()
      }
      case s if s == "today" => {
       new Date()
      }
      case _ => {
        val df = new SimpleDateFormat("yyyyMMddHHmmss")
        df.parse(startDate)
      }
    }
    val query = ContentQueries.liveContentByAuthorBeforeDate(author,typ,date)
    Content.find(query, Json.obj("dateCreated" -> -1 ), max).map{ posts =>
      Ok(toJson(posts))
    }
  }



}
