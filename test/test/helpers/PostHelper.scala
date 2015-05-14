package test.helpers

import java.text.SimpleDateFormat
import java.util.{UUID, Date}

import com.daoostinboyeez.git.GitRepo
import data.{Tags, Content}
import models.{ContentTag, ContentTypeMap, ContentPost}
import play.api.Logger
import play.api.Play.current
import play.api.db.DB



import scala.slick.jdbc.JdbcBackend._
/**
 * Created by andrew on 14/02/15.
 */
object PostHelper {
  val repo = GitRepo.apply()
  def database = Database.forDataSource(DB.getDataSource())


  def getPost(id:String) = {
    database.withSession{ implicit sess :Session =>
      val rawPost = Content.getById(id)
      rawPost
    }
  }

  def createPost(title:String, author:String, content :String, typ: Int) :ContentPost = {
    createPost(title, author, content, typ, "")
  }

  def createPostWithTags(title:String, content :String, typ: Int,tags :String) :ContentPost = {

    database.withSession {  implicit sess :Session  =>
      val post = createPost(title, "PostHelper", content, typ, "")//Need to actually make the tags
      tags.split(",").foreach{ s =>
        val tag = Tags.create(s.trim)
        Tags.link(post.id, tag.id)
      }
      post
    }

  }


  def createPost(title:String, author:String, content :String, typ: Int, extraData :String):ContentPost = {
    createPost(title, author, content, typ, extraData, new Date())
  }

  def createPost(title:String, author:String, content :String, typ: Int, extraData :String, date: Date):ContentPost = {
    val filename = repo.createFile(content)
    val post = new ContentPost(UUID.randomUUID().toString(),title, typ, date, author, filename, extraData,false)
    database.withSession { implicit s :Session =>
      Content.insert(post)
    }
    post
  }

  def createPost(title:String, author:String, content :String, typ: Int, extraData :String, date: String):ContentPost = {
    val df = new SimpleDateFormat("yyyyMMddHHmmss")
    createPost(title, author, content, typ, "", df.parse(date))
  }

  def createBiography(name:String, text :String, thumb :String) = {
    val filename = repo.createFile(text)
    val post = new ContentPost(UUID.randomUUID().toString(),name, ContentTypeMap("Biography"), new Date(), "", filename, ContentPost.extraDataToJson(s"thumb=$thumb"), false)
    val p = database.withSession { implicit s :Session =>
      Content.insert(post)
    }
    post
  }

  def createDiscographyItem(name:String, text :String, thumb :String, albumType :String) = {
    val filename = repo.createFile(text)
    val post = new ContentPost(UUID.randomUUID().toString(),name, ContentTypeMap("Discography"), new Date(), "", filename, ContentPost.extraDataToJson(s"thumb=$thumb,discType=$albumType"), false)
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

  def createDraft(title:String, author:String, content :String, typ: Int) :ContentPost = {
    createDraft(title, author, content, typ, "")
  }


  def createDraft(title:String, author:String, content :String, typ: Int, extraData :String):ContentPost = {
    createDraft(title, author, content, typ, "", new Date())
  }

  def createDraft(title:String, author:String, content :String, typ: Int, extraData :String, date: Date):ContentPost = {
    val filename = repo.createFile(content)
    val post = new ContentPost(UUID.randomUUID().toString(),title, typ, date, author, filename, extraData,true)
    database.withSession { implicit s :Session =>
      Content.insert(post)
    }
    post
  }

  def createDraft(title:String, author:String, content :String, typ: Int, extraData :String, date: String):ContentPost = {
    val df = new SimpleDateFormat("yyyyMMddHHmmss")
    createDraft(title, author, content, typ, "", df.parse(date))
  }

}
