package controllers

import java.text.SimpleDateFormat
import java.util.Date

import _root_.data.{Content, ContentQueries}
import com.daoostinboyeez.git.GitRepo
import models._
import play.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.{Concurrent, Enumerator, Iteratee}
import play.api.libs.json.Json._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.WebSocket.FrameFormatter
import play.api.mvc._
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.json._
import reactivemongo.api.QueryOpts
import reactivemongo.bson.BSONObjectID
import play.api.Play.current

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

/**
 * Created by andrew on 07/09/14.
 */
object WebSocketJsonApi extends Controller {

 import models.JsonFormats._
  //implicit val tagFormat = Json.format[ContentTag]
  implicit val commitFormat =  Json.format[ContentMeta]
  val repo = GitRepo.apply()


  def wsTest = WebSocket.using[JsValue] { request =>
    val (out, channel) = Concurrent.broadcast[JsValue]

    val in = Iteratee.foreach[JsValue]{ message =>
      Logger.info(message.toString())
      val post = new Post(BSONObjectID.generate,"Some Post Title",1,new Date,"Andrew","Leeeeeek",None,false,None,None)
      channel push(toJson(post))
    }
    (in,out)
  }
  val collection = ReactiveMongoPlugin.db.collection[JSONCollection]("leek")


  case class Leek(_id:Option[BSONObjectID],leek :String)

  implicit val format = Json.format[Leek]
  implicit val frameFormatter = FrameFormatter.jsonFrame[Leek]

  def queryTest = WebSocket.using[Leek] { request =>
    val cursor = collection.find(Json.obj()).options(QueryOpts().batchSize(4)).cursor[Leek]

    val (leek,chan) = Concurrent.broadcast[Leek]

    val out = {
      Logger.info("Enumerating")
      cursor.enumerate(10) >>> cursor.enumerate(4) >>> leek
    }


    val sheek = out >>> leek
    val in = Iteratee.foreach[Leek] { l =>
      println(l.toString())
      collection.insert(l).map { result =>
        result.ok match {
          case true =>
            Logger.info ("Leeeeeeeeeeeeeeek " + l.toString)
            cursor.collect[List](2).map { res =>

              res.foreach { doc =>
                chan push doc
              }
            }
          case false =>
            Logger.info("faild to insert docs ")
        }
      }
    }

    (in,out)
  }


  def qTest = WebSocket.using[Leek] { request =>
    val out = {
      val cursor = collection.find(Json.obj()).cursor[Leek]
      cursor.enumerate()
    }
    val (leek,chan) = Concurrent.broadcast[Leek]

    val sheek = out >>> leek
    val in = Iteratee.foreach[Leek] { l =>
      println(l.toString())
      collection.insert(l).map { result =>
        result.ok match {
          case true =>
            Logger.info ("Leeeeeeeeeeeeeeek " + l.toString)
            chan.push (l)
          case false =>
            Logger.info("faild to insert docs ")
        }
      }
    }

    (in,sheek)
  }
}
