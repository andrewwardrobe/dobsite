package controllers


import java.text.SimpleDateFormat
import java.util.Date

import data.Content
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


  //implicit val tagFormat = Json.format[ContentTag]
  implicit val newsFormat =  Json.format[ContentPost]
  implicit val commitFormat =  Json.format[ContentMeta]
  val repo = GitRepo.apply()



  def getNews = DBAction { implicit response =>
    val newsList = blogToJson(Content.getNews)
    Ok(toJson(newsList))
  }

  def getContentByType(contentType: Int) = DBAction { implicit response =>
    val newsList = blogToJson(Content.getByType(contentType))
    Ok(toJson(newsList))
  }
  def getPostById(id: String) = DBAction { implicit response =>
    val regex = """[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}""".r
     id match {
      case regex() => {
        val posts = Content.getById(id)
        if( posts.length > 0 )
          Ok(toJson(posts.head.json))
        else
          BadRequest("Not Found")
      }
      case _ => {
        val title = id.replace("_"," ")
        val posts = Content.getByTitle(title)
        if( posts.length > 0 )
          Ok(toJson(posts.head.json))
        else
          BadRequest("Not Found")
      }
    }

  }

  def getContentTags(id:String) = DBAction { implicit response =>
    val json = Content.getTagsAsJson(id)
    Ok(toJson(json))
  }

  def getPostRevisionById(id: String, revId : String) = DBAction { implicit response =>
    Ok(toJson(Content.getById(id).head.json(revId)))
  }

  def getRevisions(id: String) =  DBAction { implicit response =>
    val post = Content.getById(id)
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
    val post = Content.getById(id)
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

  def blogToJson(blogs :Seq[ContentPost]) = {
    val blogsJson = ListBuffer[JsValue]()
    blogs.foreach(blogsJson += _.json)
    blogsJson.toList
  }

  def getNewsByRange(start: String, num: Int) = DBAction { implicit response =>
    if(start != -1) {
      val newsList = blogToJson(Content.getXNewsItemsFromId(start, num))
      Ok(toJson(newsList))
    }else {
      val newsList = blogToJson(Content.getXNewsItems(num))
      Ok(toJson(newsList))
    }
  }

  def getContentByDate(typ: Int)  = DBAction { implicit response =>
    val contentList = blogToJson(Content.getByDate(typ))
    Ok(toJson(contentList))
  }


  def getContentByUserLatestFirst(id:Int) = DBAction { implicit response=>
    val posts = Content.getLiveContentByUserLatestFirst(id)
    Ok(toJson(blogToJson(posts)))
  }
  def getContentByDateStart(typ: Int,startDate: String,max :Int)  = DBAction { implicit response =>
    val contentList = startDate match {
      case s if s.isEmpty() || s == "" => { blogToJson(Content.getByDate(typ,new Date(),max)) }
      case s if s == "today" => { blogToJson(Content.getByDate(typ,new Date(),max)) }
      case _ => {
        val df = new SimpleDateFormat("yyyyMMddHHmmss")
        blogToJson(Content.getByDate(typ,df.parse(startDate),max))
      }
    }
    Ok(toJson(contentList))
  }





}
