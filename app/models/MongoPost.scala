package models

import java.util.Date

import com.daoostinboyeez.git.GitRepo
import org.eclipse.jgit.errors.RevisionSyntaxException
import reactivemongo.bson.BSONObjectID

/**
 *
 * Created by andrew on 29/07/15.
 *
 */
case class MongoPost(_id: BSONObjectID, title:String, postType: Int, dateCreated :Date,author:String,
                     content: String, extraData: String, isDraft: Boolean, userId:Option[BSONObjectID], tags: Option[Seq[String]]){

  def revision(revision :String, repo : GitRepo) ={
    //Todo populate this from json commit message or store
    //the json in the git files
    val revContent = repo.getFile(_id.stringify,revision)
    this.copy(content = revContent)
  }

  def extraDataToJson  = {
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
    val json = str.toString().replace(",}", "}")
    json
  }

  def cleanUp = {
    //Todo Jsoup cleaning
    lkl;
  }

}


