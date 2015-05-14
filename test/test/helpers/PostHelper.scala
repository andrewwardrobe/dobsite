package test.helpers

import java.text.SimpleDateFormat
import java.util.{UUID, Date}

import com.daoostinboyeez.git.GitRepo
import data.{Tags, Posts, PostToTagDAO}
import models.{ContentTag, PostTypeMap, Post,PostToTagLink}
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
      val rawPost = Posts.getById(id)
      rawPost
    }
  }

  def createPost(title:String, author:String, content :String, typ: Int) :Post = {
    createPost(title, author, content, typ, "")
  }

  def createPostWithTags(title:String, content :String, typ: Int,tags :String) :Post = {

    database.withSession {  implicit sess :Session  =>
      val post = createPost(title, "PostHelper", content, typ, "")//Need to actually make the tags
      tags.split(",").foreach{ s =>
        val tag = Tags.create(s.trim)
        PostToTagDAO.link(post.id, tag.id)
      }
      post
    }

  }


  def createPost(title:String, author:String, content :String, typ: Int, extraData :String):Post = {
    createPost(title, author, content, typ, extraData, new Date())
  }

  def createPost(title:String, author:String, content :String, typ: Int, extraData :String, date: Date):Post = {
    val filename = repo.createFile(content)
    val post = new Post(UUID.randomUUID().toString(),title, typ, date, author, filename, extraData,false)
    database.withSession { implicit s :Session =>
      Posts.insert(post)
    }
    post
  }

  def createPost(title:String, author:String, content :String, typ: Int, extraData :String, date: String):Post = {
    val df = new SimpleDateFormat("yyyyMMddHHmmss")
    createPost(title, author, content, typ, "", df.parse(date))
  }

  def createBiography(name:String, text :String, thumb :String) = {
    val filename = repo.createFile(text)
    val post = new Post(UUID.randomUUID().toString(),name, PostTypeMap("Biography"), new Date(), "", filename, Post.extraDataToJson(s"thumb=$thumb"), false)
    val p = database.withSession { implicit s :Session =>
      Posts.insert(post)
    }
    post
  }

  def createDiscographyItem(name:String, text :String, thumb :String, albumType :String) = {
    val filename = repo.createFile(text)
    val post = new Post(UUID.randomUUID().toString(),name, PostTypeMap("Discography"), new Date(), "", filename, Post.extraDataToJson(s"thumb=$thumb,discType=$albumType"), false)
    val p = database.withSession { implicit s :Session =>
      Posts.insert(post)
    }
    post
  }

  def clearAll = {
    database.withSession { implicit s :Session =>
      Posts.clearAll
    }
  }

  def createDraft(title:String, author:String, content :String, typ: Int) :Post = {
    createDraft(title, author, content, typ, "")
  }


  def createDraft(title:String, author:String, content :String, typ: Int, extraData :String):Post = {
    createDraft(title, author, content, typ, "", new Date())
  }

  def createDraft(title:String, author:String, content :String, typ: Int, extraData :String, date: Date):Post = {
    val filename = repo.createFile(content)
    val post = new Post(UUID.randomUUID().toString(),title, typ, date, author, filename, extraData,true)
    database.withSession { implicit s :Session =>
      Posts.insert(post)
    }
    post
  }

  def createDraft(title:String, author:String, content :String, typ: Int, extraData :String, date: String):Post = {
    val df = new SimpleDateFormat("yyyyMMddHHmmss")
    createDraft(title, author, content, typ, "", df.parse(date))
  }

}
