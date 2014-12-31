package controllers



import models._
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json.{JsValue, Json}
import play.api.libs.json.Json._

import scala.collection.mutable.ListBuffer
import scala.util.parsing.json.JSONArray


/**
 * Created by andrew on 07/09/14.
 */
object JsonApi extends Controller {

  implicit val discoFormat =  Json.format[Discography]
  implicit val trackFormat =  Json.format[Track]
  implicit val bioFormat =  Json.format[Biography]
  implicit val newsFormat =  Json.format[Blog]

  def getDiscographyByReleaseType(_type: Int) = DBAction { implicit response =>
      Ok(toJson(Discography.getByReleaseType(_type)))
  }

  def getNews = DBAction { implicit response =>
    val newsList = blogToJson(Blog.getNews)
    Ok(toJson(newsList))
  }

  def getContentByType(contentType: Int) = DBAction { implicit response =>
    val newsList = blogToJson(Blog.getByType(contentType))
    Ok(toJson(newsList))
  }
  def getPostById(id: Int) = DBAction { implicit response =>
    Ok(toJson(Blog.getById(id).head.json))
  }

  def blogToJson(blogs :Seq[Blog]) = {
    val blogsJson = ListBuffer[JsValue]()
    blogs.foreach(blogsJson += _.json)
    blogsJson.toList
  }

  def getNewsByRange(start: Int, num: Int) = DBAction { implicit response =>
    if(start != -1) {
      val newsList = blogToJson(Blog.getXNewsItemsFromId(start, num))
      Ok(toJson(newsList))
    }else {
      val newsList = blogToJson(Blog.getXNewsItems(num))
      Ok(toJson(newsList))
    }
  }

  def getContentByRange(typ:Int, start: Int, num: Int) = DBAction { implicit response =>
    if(start != -1) {
      val content = blogToJson(Blog.getXItemsFromId(typ,start, num))
      Ok(toJson(content))
    }else {
      val content = blogToJson(Blog.getXItems(typ,num))
      Ok(toJson(content))
    }
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
