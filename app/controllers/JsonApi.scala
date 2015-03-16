package controllers


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
  implicit val bioFormat =  Json.format[Biography]
  implicit val newsFormat =  Json.format[Post]
  implicit val commitFormat =  Json.format[CommitMeta]
  val repo = GitRepo.apply()

  def getDiscographyByReleaseType(_type: Int) = DBAction { implicit response =>
      Ok(toJson(Discography.getByReleaseType(_type)))
  }

  def getNews = DBAction { implicit response =>
    val newsList = blogToJson(Post.getNews)
    Ok(toJson(newsList))
  }

  def getContentByType(contentType: Int) = DBAction { implicit response =>
    val newsList = blogToJson(Post.getByType(contentType))
    Ok(toJson(newsList))
  }
  def getPostById(id: Int) = DBAction { implicit response =>
    Ok(toJson(Post.getById(id).head.json))
  }

  def getPostRevisionById(id: Int, revId : String) = DBAction { implicit response =>
    Ok(toJson(Post.getById(id).head.json(revId)))
  }

  def getRevisions(id: Int) =  DBAction { implicit response =>
    val post = Post.getById(id)
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

  def getRevisionsWithDates(id: Int) =  DBAction { implicit response =>
    val post = Post.getById(id)
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

  def getNewsByRange(start: Int, num: Int) = DBAction { implicit response =>
    if(start != -1) {
      val newsList = blogToJson(Post.getXNewsItemsFromId(start, num))
      Ok(toJson(newsList))
    }else {
      val newsList = blogToJson(Post.getXNewsItems(num))
      Ok(toJson(newsList))
    }
  }

  def getContentByRange(typ:Int, start: Int, num: Int) = DBAction { implicit response =>
    if(start != -1) {
      val content = blogToJson(Post.getXItemsFromId(typ,start, num))
      Ok(toJson(content))
    }else {
      val content = blogToJson(Post.getXItems(typ,num))
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
