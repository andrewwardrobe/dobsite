package data

import models.MongoPost
import play.api.Play.current
import java.util.Date

import play.api.libs.json.Json
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
object PostDAO {
  import play.modules.reactivemongo.json.BSONFormats._
  private implicit val postFormat = Json.format[MongoPost]

  private lazy val posts = ReactiveMongoPlugin.db.collection[JSONCollection]("posts")

  def get(title: String) = {
    posts.find(Json.obj("title" -> title)).cursor[MongoPost].collect[List]()
  }
}
