package models

import java.util.Date

import com.daoostinboyeez.git.GitRepo
import org.eclipse.jgit.errors.RevisionSyntaxException
import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import org.jsoup._
import org.jsoup.safety.Whitelist

import scala.collection.mutable

/**
 *
 * Created by andrew on 29/07/15.
 *
 */
case class Post(_id: BSONObjectID, title:String, postType: Int, dateCreated :Date,author:String,
                     content: String, extraData: Option[Map[String,String]], isDraft: Boolean, userId:Option[BSONObjectID], tags: Option[Seq[String]]){

  def revision(revision :String, repo : GitRepo) ={
    //Todo populate this from json commit message or store
    //the json in the git files
    val revContent = repo.getFile(_id.stringify,revision)
    revContent match {
      case "" => this
      case _ => this.copy(content = revContent)
    }
  }

  def extraDataToJson  = {
    Json.toJson(extraData)
  }

  def getCleanContent = {
    //Todo Jsoup cleaning

    Jsoup.clean(content,Whitelist.basicWithImages()
      .preserveRelativeLinks(true)
      .addAttributes("img","class")
      .addAttributes("p","class")
      .addAttributes("div","align"))

  }

  def id = _id.stringify

}

object Post {
  def stringToMap(data:String) = {
    if (data == "")
      None
    else {
      val map = new mutable.HashMap[String, String]()
      data.split("\n").foreach { line =>
        val parts = line.split("=")
        if (parts.length > 1)
          map.put(parts(0), parts(1))
        else if(parts.length == 1)
          map.put(parts(0), "")
      }
      val returnMap = new scala.collection.immutable.HashMap[String, String]() ++ map
      Some(returnMap)
    }
  }

  def mapToString(mapOpt: Option[Map[String,String]]) = {
    mapOpt match {
      case None => ""
      case Some(map) =>
        val str = new StringBuilder()
        map.foreach { (tuple) =>
          str.append(s"${tuple._1}=${tuple._2}\n")
        }
        str.toString()
    }
  }
}


