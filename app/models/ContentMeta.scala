package models

import com.daoostinboyeez.site.exceptions.InvalidMetaJsonException
import org.apache.commons.lang3.StringUtils
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Json, Writes}

/**
 * Created by andrew on 07/03/15.
 */
case class ContentMeta(commitId: String,commitDate :String,extraData :String){
  def this(commitId: String,commitDate :String) = this(commitId,commitDate,"")

}

object ContentMeta {
  //implicit val tagFormat = Json.format[ContentTag]
  import models.JsonFormats._


  def getMeta(commitMsg:String) = {
    val  meta =  StringUtils.substringBetween(commitMsg,"++++META++++\n","\n----META----")
    if (meta != null)
      meta
    else
      ""
  }

  def toPost(commit:String) = {
    val meta = getMeta(commit)
    if(meta!=null && meta!="") {
      val json = Json.parse(meta)
      val post = Json.fromJson[Post](json) match {
        case p: JsSuccess[Post] => {
          p.get
        }
        case err: JsError => {
          throw new InvalidMetaJsonException("Error Deserialising Json :" + JsError.toFlatJson(err).toString())
        }
      }
      post
    }
    else
      null
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

  def fromPost(post: Post) = new ContentMeta("","",toMeta(post))

}
