package controllers


import java.text.SimpleDateFormat
import java.util.Date

import _root_.data.Posts
import com.daoostinboyeez.git.GitRepo
import models._
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.json.Json._

import scala.collection.mutable.ListBuffer
import scala.util.parsing.json.{JSONObject, JSONArray}


/**
 * Created by andrew on 07/09/14.
 */
object JsonApi extends Controller {

  implicit val discoFormat =  Json.format[Discography]
  implicit val trackFormat =  Json.format[Track]
  implicit val bioFormat =  Biography.jsonFormat;
  //implicit val tagFormat = Json.format[ContentTag]
  implicit val newsFormat =  Json.format[Post]
  implicit val commitFormat =  Json.format[PostMeta]
  val repo = GitRepo.apply()

  def getDiscographyByReleaseType(_type: Int) = DBAction { implicit response =>
      Ok(toJson(Discography.getByReleaseType(_type)))
  }

  def getNews = DBAction { implicit response =>
    val newsList = blogToJson(Posts.getNews)
    Ok(toJson(newsList))
  }

  def getContentByType(contentType: Int) = DBAction { implicit response =>
    val newsList = blogToJson(Posts.getByType(contentType))
    Ok(toJson(newsList))
  }
  def getPostById(id: String) = DBAction { implicit response =>
    val regex = """[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}""".r
     id match {
      case regex() => {
        val posts = Posts.getById(id)
        if( posts.length > 0 )
          Ok(toJson(posts.head.json))
        else
          BadRequest("Not Found")
      }
      case _ => {
        val title = id.replace("_"," ")
        val posts = Posts.getByTitle(title)
        if( posts.length > 0 )
          Ok(toJson(posts.head.json))
        else
          BadRequest("Not Found")
      }
    }

  }

  def getContentTags(id:String) = DBAction { implicit response =>
    val json = Posts.getTagsAsJson(id)
    Ok(toJson(json))
  }

  def getPostRevisionById(id: String, revId : String) = DBAction { implicit response =>
    Ok(toJson(Posts.getById(id).head.json(revId)))
  }

  def getRevisions(id: String) =  DBAction { implicit response =>
    val post = Posts.getById(id)
      post.isEmpty match{
        case false => {
          val filename = post.head.content
          val revisions = repo.findRevDates(filename)
          revisions.isEmpty match {
            case false => {
              Ok(toJson(revisions))
            }
            case true => {
              Ok(toJson("None"))
            }
          }
        }
        case true => Ok(toJson("None"))
      }

  }

  def getRevisionsWithDates(id: String) =  DBAction { implicit response =>
    val post = Posts.getById(id)
    post.isEmpty match{
      case false => {
        val filename = post.head.content
        val revisions = repo.findWithDate(filename)
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
      case true => Ok(toJson("None"))
    }

  }

  def blogToJson(blogs :Seq[Post]) = {
    val blogsJson = ListBuffer[JsValue]()
    blogs.foreach(blogsJson += _.json)
    blogsJson.toList
  }

  def getNewsByRange(start: String, num: Int) = DBAction { implicit response =>
    if(start != -1) {
      val newsList = blogToJson(Posts.getXNewsItemsFromId(start, num))
      Ok(toJson(newsList))
    }else {
      val newsList = blogToJson(Posts.getXNewsItems(num))
      Ok(toJson(newsList))
    }
  }

  def getContentByDate(typ: Int)  = DBAction { implicit response =>
    val contentList = blogToJson(Posts.getByDate(typ))
    Ok(toJson(contentList))
  }

  def getContentByDateStart(typ: Int,startDate: String,max :Int)  = DBAction { implicit response =>
    val contentList = startDate match {
      case s if s.isEmpty() || s == "" => { blogToJson(Posts.getByDate(typ,new Date(),max)) }
      case s if s == "today" => { blogToJson(Posts.getByDate(typ,new Date(),max)) }
      case _ => {
        val df = new SimpleDateFormat("yyyyMMddHHmmss")
        blogToJson(Posts.getByDate(typ,df.parse(startDate),max))
      }
    }
    Ok(toJson(contentList))
  }

  def getDiscography = DBAction { implicit response =>
    Ok(toJson(Discography.get))
  }

  def getRelease(id: Int) = DBAction { implicit response =>
    Ok(toJson(Discography.getById(id)))
  }

  def getTracksByReleaseId(relId: Int) = DBAction {implicit response =>
    Ok(toJson(Track.getByReleaseId(relId)))

  }

  def getBioByType(bioType: Int) = DBAction { implicit response =>
    Ok(toJson(Biography.getByType(bioType)))
  }


  def getBioById(id: Int) = DBAction { implicit response =>
    Ok(toJson(Biography.getByType(id)))
  }

  def getBiography() = DBAction { implicit response =>
     Ok(toJson(Biography.getAll))
  }
}
