package models

import java.util.{UUID, Date}

import org.jsoup._
import org.jsoup.safety.Whitelist
import play.api.{Play, Logger}
import play.api.data.Form
import play.api.data.Forms._

import play.api.libs.json.{JsString, Json, JsValue, JsObject, JsNumber}

import scala.collection.mutable.ListBuffer

/**
 * Created by andrew on 11/10/14.
 */
case class Discography(title:String,artwork:String,discType: String,tracks: Seq[String])