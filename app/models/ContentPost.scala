package models

import java.util.{UUID, Date}
import com.daoostinboyeez.git.GitRepo
import data.{Content, ContentPostSchema, ContentAccessFunctions}
import org.jsoup._
import org.jsoup.safety.Whitelist
import play.api.{Play, Logger}
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.libs.json.{JsString, Json, JsValue, JsObject, JsNumber}

import scala.collection.mutable.ListBuffer

/**
 * Created by andrew on 11/10/14.
 */
case class ContentPost(id: String, title: String, postType: Int, dateCreated: Date, author: String, content: String, extraData: String,isDraft: Boolean,userId:Option[Int]) {

  val repo = GitRepo.apply()
  def json: JsValue = Json.obj(
     "id" -> id,
      "title" -> title,
    "postType" -> postType,
    "dateCreated" -> dateCreated,
    "author" -> author,
   "content" -> JsString(Jsoup.clean(getContent(),"http://localhost:9000/",Whitelist.basicWithImages()
                                                                      .preserveRelativeLinks(true)
                                                                      .addAttributes("img","class")
                                                                      .addAttributes("p","class")
                                                                      .addAttributes("div","align")
                                     )),
    "extraData" -> extraData,
    "isDraft" -> isDraft,
    "userId" -> userId
  )

  def json(rev :String): JsValue = Json.obj(
    "id" -> id,
    "title" -> title,
    "postType" -> postType,
    "dateCreated" -> dateCreated,
    "author" -> author,
    "content" -> JsString(Jsoup.clean(getContent(rev),"http://localhost:9000/",Whitelist.basicWithImages()
      .preserveRelativeLinks(true)
      .addAttributes("img","class")
      .addAttributes("p","class")
      .addAttributes("div","align")
    )),
    "extraData" -> extraData,
    "isDraft" -> isDraft,
    "userId" -> userId
  )

  def getContent() = {
    repo.getFile(content)
  }

  def getContent(commitId :String) = {
    repo.getFile(content,commitId)
  }

  def tags(implicit s: Session) = {
    val lb = new ListBuffer[String]
    Content.getTags(id).foreach { t =>
      lb += t.title
    }
    lb.toList
  }
}

object ContentPost {

  def extraDataToJson(extraData:String)  = {
    val str : StringBuilder = new StringBuilder
      str.append("{")
    extraData.split("\n").foreach{
      s =>
        val parts = s.split("=")
        if(parts.length > 1) {
          str.append("\"" + parts(0) + "\":"+"\""+parts(1)+"\",")
        }
    }
    str.append("}")
    str.toString().replace(",}","}")
  }

  val blogForm: Form[ContentPost] = Form {
    mapping (
      "id" -> text,
      "title" -> text,
      "postType" -> number,
      "dateCreated" -> date,
      "author" ->text,
      "content" -> text,
      "extraData" -> text,
      "isDraft" -> boolean,
      "userId" -> optional(number)
    )(ContentPost.apply)(ContentPost.unapply _)
  }


}



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

