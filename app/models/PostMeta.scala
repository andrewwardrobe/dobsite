package models

import com.daoostinboyeez.site.exceptions.InvalidMetaJsonException
import org.apache.commons.lang3.StringUtils
import play.api.libs.json.{JsError, JsSuccess, Json, Writes}

/**
 * Created by andrew on 07/03/15.
 */
case class PostMeta(commitId: String,commitDate :String,extraData :String){
  def this(commitId: String,commitDate :String) = this(commitId,commitDate,"")

}

object PostMeta {
  implicit val tagFormat = Json.format[Tags]
  implicit val postFormat = Json.format[Post]


  def getMeta(commitMsg:String) = {
    val  meta =  StringUtils.substringBetween(commitMsg,"++++META++++\n","\n----META----")
    meta
  }

  def toPost(meta:String) = {
    val json = Json.parse(getMeta(meta))
    Json.fromJson[Post](json) match {
      case p: JsSuccess[Post] => p.get
      case err: JsError => throw new InvalidMetaJsonException("Error Deserialising Json :" +JsError.toFlatJson(err).toString())
    }
  }

  def makeCommitMsg(commitMsg: String, post: Post) = {
    val str = new StringBuilder
    str.append(commitMsg)
    str.append("\n\n")
    str.append(toMeta(post))
    str.toString()
  }

  def toMeta(post: Post) = {
    val metaStr = new StringBuilder()
    metaStr.append("++++META++++")
    metaStr.append("\n")
    metaStr.append(Json.toJson(post).toString())
    metaStr.append("\n")
    metaStr.append("----META----")
    metaStr.toString()
  }

  def fromPost(post: Post) = new PostMeta("","",toMeta(post))

}
