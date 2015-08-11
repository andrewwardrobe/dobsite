package data

import models.Post
import play.api.Play.current
import java.util.{NoSuchElementException, Date}

import play.api.libs.json._

import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.{ReactiveMongoPlugin, ReactiveMongoHelper}
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.ImplicitBSONHandlers
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

/**
 *
 * Created by andrew on 29/07/15.
 *
 */



object PostDAO extends DAOBase[Post]("posts"){

  import models.JsonFormats._
  def get(title: String) = {
    this.find(Json.obj("title" -> title))
  }
}
