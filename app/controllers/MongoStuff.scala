package controllers

import data.PostDAO
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
import models.MongoPost
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
    val postList = posts.find(Json.obj("_id" -> Json.obj("$oid" -> id))).cursor[MongoPost].collect[List]()
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
}
