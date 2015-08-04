package models

import java.util.{UUID, Date}
import data.{Content}
import play.api.{Play, Logger}
import play.api.libs.json.{JsString, Json, JsValue, JsObject, JsNumber}




import play.api.Play.current
object ContentTypeMap {
  private lazy val typeMap = {
    val tmp = scala.collection.mutable.Map[Int, String]()
    Play.configuration.getString("contenttypes.map").getOrElse("").split(",").map(s => s.trim).toList.foreach{ str =>
      if(str != "") {
        val parts = str.split("->").map(s => s.trim).toList
        if(parts.length == 2)
          tmp += (parts(0).toInt -> parts(1))
      }
    }
    scala.collection.immutable.Map[Int, String](tmp.toSeq:_*)
  }
  private lazy val revMap = typeMap.map(_.swap)



  def allRoles = typeMap.values.toList

  def apply(key:String) = get(key)

  def apply(key:Int) = get(key)

  def get(key:String) = revMap.getOrElse(key,-1)

  def get(key:Int) = typeMap.getOrElse(key,"")
}

