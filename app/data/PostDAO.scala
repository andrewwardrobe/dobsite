package data

import models.MongoPost
import play.api.Play.current
import java.util.Date

import play.api.libs.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.{ReactiveMongoPlugin, ReactiveMongoHelper}
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.ImplicitBSONHandlers
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 *
 * Created by andrew on 29/07/15.
 *
 */

class DAOBase[T](val collectionName : String) {

  protected lazy val collection = ReactiveMongoPlugin.db.collection[JSONCollection](collectionName)

  def find(query :JsObject)(implicit format: Format[T]) = {
    collection.find(query).cursor[T].collect[List]() //put some error handling in here
  }

  def getById(id: String)(implicit format: Format[T]) ={
    collection.find(Json.obj("_id" -> Json.obj("$oid" -> id))).cursor[T].collect[List]()
  }
}

object PostDAO extends DAOBase[MongoPost]("posts"){

  import models.JsonFormats._
  def get(title: String) = {
    this.find(Json.obj("title" -> title))
  }
}
