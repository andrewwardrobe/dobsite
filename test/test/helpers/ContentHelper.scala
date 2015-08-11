package test.helpers

import java.text.SimpleDateFormat
import java.util.{UUID, Date}

import com.daoostinboyeez.git.GitRepo
import data.{Content}
import models.{Post, ContentTypeMap}
import play.api.Logger
import play.api.Play.current
import play.api.db.DB
import reactivemongo.bson.BSONObjectID


import scala.concurrent.Await
import scala.slick.jdbc.JdbcBackend._
import scala.concurrent.duration.DurationInt
/**
 * Created by andrew on 14/02/15.
 */
object ContentHelper {
  import models.JsonFormats._

  val repo = GitRepo.apply()
  def database = Database.forDataSource(DB.getDataSource())


  def getPost(id:String) = {
    Await.result(Content.findById(id),10 seconds)
  }

  def createPost(title:String, author:String, content :String, typ: Int,userId:Option[BSONObjectID]) :Post = {
    createPost(title, author, content, typ, "",userId)
  }

  def createPostWithTags(title:String, content :String, typ: Int,tags :String,userId:Option[BSONObjectID]) :Post = {


      val tagList = tags match {
        case "" => None
        case _ => Some(tags.split(",").toList)
      }
      val post = createPost(title, "PostHelper", content, typ, "",new Date,userId,tagList)//Need to actually make the tags
    post

  }


  def createPost(title:String, author:String, content :String, typ: Int, extraData :String,userId:Option[BSONObjectID]):Post = {
    createPost(title, author, content, typ, extraData, new Date(),userId)
  }

  def createPost(title:String, author:String, content :String, typ: Int, extraData :String, date: Date,userId:Option[BSONObjectID],tags : Option[Seq[String]] = None):Post = {
    val post = new Post(BSONObjectID.generate, title, typ, date, author, content, extraData, false, userId, tags)
    Await.result(Content.create(post, repo), 10 seconds)
  }

  def createPost(title:String, author:String, content :String, typ: Int, extraData :String, date: String,userId:Option[BSONObjectID]):Post = {
    val df = new SimpleDateFormat("yyyyMMddHHmmss")
    createPost(title, author, content, typ, "", df.parse(date),userId)
  }

  def createBiography(name:String, text :String, thumb :String,userId:Option[BSONObjectID]) = {
    val post = new Post(BSONObjectID.generate, name, ContentTypeMap("Biography"), new Date(), "", text, s"thumb=$thumb", false, userId, None)
    Await.result(Content.create(post, repo), 10 seconds)
  }

  def createDiscographyItem(name:String, text :String, thumb :String, albumType :String,userId:Option[BSONObjectID]) = {
    val post = new Post(BSONObjectID.generate, name, ContentTypeMap("Discography"), new Date(), "", text, s"thumb=$thumb\ndiscType=$albumType", false, userId,None)
    Await.result(Content.create(post, repo), 10 seconds)
  }

  def clearAll = {
    Await.result(Content.deleteAll,10 seconds)
  }

  def createDraft(title:String, author:String, content :String, typ: Int,userId:Option[BSONObjectID]) :Post = {
    createDraft(title, author, content, typ, "",userId)
  }


  def createDraft(title:String, author:String, content :String, typ: Int, extraData :String,userId:Option[BSONObjectID]):Post = {
    createDraft(title, author, content, typ, extraData, new Date(),userId)
  }

  def createDraft(title:String, author:String, content :String, typ: Int, extraData :String, date: Date,userId:Option[BSONObjectID]):Post = {
    val post = new Post(BSONObjectID.generate, title, typ, date, author, content, extraData, true, userId,None)
    Await.result(Content.create(post, repo),10 seconds)
  }

  def createDraft(title:String, author:String, content :String, typ: Int, extraData :String, date: String,userId:Option[BSONObjectID]):Post = {
    val df = new SimpleDateFormat("yyyyMMddHHmmss")
    createDraft(title, author, content, typ, extraData, df.parse(date),userId)
  }

}
