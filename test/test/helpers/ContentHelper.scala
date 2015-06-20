package test.helpers

import java.text.SimpleDateFormat
import java.util.{UUID, Date}

import com.daoostinboyeez.git.GitRepo
import data.{Tags, Content}
import models.{ContentMeta, ContentTag, ContentTypeMap, ContentPost}
import play.api.Logger
import play.api.Play.current
import play.api.db.DB



import scala.slick.jdbc.JdbcBackend._
/**
 * Created by andrew on 14/02/15.
 */
object ContentHelper {
  val repo = GitRepo.apply()
  def database = Database.forDataSource(DB.getDataSource())


  def getPost(id:String) = {
    database.withSession{ implicit sess :Session =>
      val rawPost = Content.getById(id)
      rawPost
    }
  }

  def createPost(title:String, author:String, content :String, typ: Int,userId:Option[Int]) :ContentPost = {
    createPost(title, author, content, typ, "",userId)
  }

  def createPostWithTags(title:String, content :String, typ: Int,tags :String,userId:Option[Int]) :ContentPost = {

    database.withSession {  implicit sess :Session  =>
      val post = createPost(title, "PostHelper", content, typ, "",userId)//Need to actually make the tags
      tags.split(",").foreach{ s =>
        val tag = Tags.create(s.trim)
        Tags.link(post.id, tag.id)
      }
      post
    }

  }


  def createPost(title:String, author:String, content :String, typ: Int, extraData :String,userId:Option[Int]):ContentPost = {
    createPost(title, author, content, typ, extraData, new Date(),userId)
  }

  def createPost(title:String, author:String, content :String, typ: Int, extraData :String, date: Date,userId:Option[Int]):ContentPost = {
    val filename = repo.genFileName

    val post = new ContentPost(UUID.randomUUID().toString(),title, typ, date, author, filename, extraData,false,userId)
    repo.doFile(filename,content,ContentMeta.makeCommitMsg("Created", post))
    database.withSession { implicit s :Session =>
      Content.insert(post)
    }
    post
  }

  def createPost(title:String, author:String, content :String, typ: Int, extraData :String, date: String,userId:Option[Int]):ContentPost = {
    val df = new SimpleDateFormat("yyyyMMddHHmmss")
    createPost(title, author, content, typ, "", df.parse(date),userId)
  }

  def createBiography(name:String, text :String, thumb :String,userId:Option[Int]) = {
    val filename = repo.genFileName
    val post = new ContentPost(UUID.randomUUID().toString(),name, ContentTypeMap("Biography"), new Date(), "", filename, ContentPost.extraDataToJson(s"thumb=$thumb"), false,userId)
    repo.doFile(filename,text,ContentMeta.makeCommitMsg("Created", post))
    val p = database.withSession { implicit s :Session =>
      Content.insert(post)
    }
    post
  }

  def createDiscographyItem(name:String, text :String, thumb :String, albumType :String,userId:Option[Int]) = {
    val filename = repo.genFileName
    val post = new ContentPost(UUID.randomUUID().toString(),name, ContentTypeMap("Discography"), new Date(), "", filename, ContentPost.extraDataToJson(s"thumb=$thumb\ndiscType=$albumType"), false,userId)
    repo.doFile(filename,text,ContentMeta.makeCommitMsg("Created", post))
    val p = database.withSession { implicit s :Session =>
      Content.insert(post)
    }
    post
  }

  def clearAll = {
    database.withSession { implicit s :Session =>
      Content.clearAll
    }
  }

  def createDraft(title:String, author:String, content :String, typ: Int,userId:Option[Int]) :ContentPost = {
    createDraft(title, author, content, typ, "",userId)
  }


  def createDraft(title:String, author:String, content :String, typ: Int, extraData :String,userId:Option[Int]):ContentPost = {
    createDraft(title, author, content, typ, extraData, new Date(),userId)
  }

  def createDraft(title:String, author:String, content :String, typ: Int, extraData :String, date: Date,userId:Option[Int]):ContentPost = {
    val filename = repo.genFileName
    val post = new ContentPost(UUID.randomUUID().toString(),title, typ, date, author, filename, extraData,true,userId)
    repo.doFile(filename,content,ContentMeta.makeCommitMsg("Created", post))
    database.withSession { implicit s :Session =>
      Content.insert(post)
    }
    post
  }

  def createDraft(title:String, author:String, content :String, typ: Int, extraData :String, date: String,userId:Option[Int]):ContentPost = {
    val df = new SimpleDateFormat("yyyyMMddHHmmss")
    createDraft(title, author, content, typ, extraData, df.parse(date),userId)
  }

}
