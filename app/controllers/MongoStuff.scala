package controllers

import data.PostDAO
import play.api.data.Form
import play.api.data.Forms._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.ImplicitBSONHandlers

import reactivemongo.bson.BSONObjectID
import reactivemongo.api.Cursor

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import jp.t2v.lab.play2.auth.OptionalAuthElement
import models.{ContentPost, MongoPost}
import play.api.libs.json.{JsValue, JsArray, JsObject, Json}
import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
/**
 *
 * Created by andrew on 29/07/15.
 *
 */
object MongoStuff extends Controller with MongoController with  OptionalAuthElement with StandardAuthConfig{
  implicit val timeout = 10.seconds

  import play.modules.reactivemongo.json.BSONFormats._
  implicit val postFormat = Json.format[MongoPost]

  def posts : JSONCollection = db.collection[JSONCollection]("posts")

  val blogForm: Form[MongoPost] = Form {
    mapping (
      "id" -> ignored(BSONObjectID.generate :BSONObjectID),
      "title" -> text,
      "postType" -> number,
      "author" ->text,
      "content" -> text,
      "extraData" -> text,
      "isDraft" -> boolean,
      "userId" -> optional(number)
    )(MongoPost.apply)(MongoPost.unapply _)
  }

  def mongoGet = Action.async {
    val postList = posts.genericQueryBuilder.cursor[MongoPost].collect[List]()
    postList.map { pstList =>
      Ok(toJson(pstList))
    }.recover{
      case ex: Exception => {
        InternalServerError("Error" +ex.getMessage)
      }
    }
  }

  def getById(id:String) = Action.async {
    val postList = PostDAO.getById(id)
    postList.map { pstList =>
      Ok(toJson(pstList))
    }.recover{
      case ex: Exception => {
        InternalServerError("Error" +ex.getMessage)
      }
    }
  }

  def getByTitle(title:String) = Action.async {
    val postList = PostDAO.get(title)
    postList.map { pstList =>
      Ok(toJson(pstList))
    }.recover{
      case ex: Exception => {
        InternalServerError("Error" +ex.getMessage)
      }
    }
  }

  def save() = Action.async {
    val leek = new MongoPost(BSONObjectID.generate,"Leek Post",1,"Andrew","Twat Bat", "", false, Some(43))
    val result = PostDAO.insert(leek)
    result.map { res =>
      Ok("Inseted" +res.toString)

    }
  }
}
