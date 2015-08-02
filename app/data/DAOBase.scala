package data

import data.UserAccounts._
import models.{UserAccount, MongoPost}
import org.mindrot.jbcrypt.BCrypt
import play.api.Play.current
import java.util.{NoSuchElementException, Date}

import play.api.libs.json._
import play.api.libs.json.Json._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.{ReactiveMongoPlugin, ReactiveMongoHelper}
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.ImplicitBSONHandlers
import play.modules.reactivemongo.json.BSONFormats._
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.bson.BSONDocument
import reactivemongo.core.commands.Count

import scala.concurrent.Future

/**
 *
 * Created by andrew on 29/07/15.
 *
 */

class DAOBase[T](val collectionName : String) {

  protected lazy val collection = ReactiveMongoPlugin.db.collection[JSONCollection](collectionName)

  def find(query :JsObject)(implicit format: OFormat[T]) = {
    val documents = collection.find(query)
    val items = try {
      val cursor = documents.cursor[T]
      cursor.collect[Vector]()
    }catch{
      case ex : NoSuchElementException =>
        Future {
          Vector()
        }
    }
    items
  }

  def getById(id: String)(implicit format: OFormat[T]) ={ //Also handle invalid ads
  val documents = collection.find(Json.obj("_id" -> Json.obj("$oid" -> id)))
    val items = try {
      val cursor = documents.cursor[T]
      cursor.collect[Vector]()
    }catch{
      case ex : NoSuchElementException =>
        Future {
          Vector()
        }
    }
    items
  }

  def insert(item: T)(implicit format: OFormat[T]) = {
    collection.insert(item)
  }

  def update(id: String, data: T)(implicit format: OFormat[T]) = {
    collection.update(Json.obj("_id" -> Json.obj("$oid" -> id)),data)
  }

  def delete(id: String)(implicit format: OFormat[T]) = {
    collection.remove(Json.obj("_id" -> Json.obj("$oid" -> id)))
  }

  def deleteAll = {
    collection.remove(Json.obj())
  }

  def count(query :JsValue) = {
    val doc = BSONFormats.toBSON(query).get.asInstanceOf[BSONDocument]
    collection.db.command(
      Count(collection.name, Some(doc))
    )
  }
}