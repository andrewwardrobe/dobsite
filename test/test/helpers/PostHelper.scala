package test.helpers

import java.util.Date

import com.daoostinboyeez.git.GitRepo
import models.Post
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

  val types = Map("News" -> 1, "Music" -> 2, "Gaz Three" -> 3, "Biography" -> 4)
  def createPost(title:String, author:String, content :String, typ: Int) :Post = {
    createPost(title, author, content, typ, "")
  }

  def createPost(title:String, author:String, content :String, typ: Int, extraData :String):Post = {
    val filename = repo.createFile(content)
    val post = new Post(1, title,typ,new Date(),author,filename,extraData)
    database.withSession { implicit s :Session =>
      Post.insert(post)
    }
    post
  }

  def createBiography(name:String, text :String, thumb :String) = {
    val filename = repo.createFile(text)
    val post = new Post(1, name, 4 ,new Date(),"",filename,Post.extraDataToJson(s"thumb=$thumb"))
    val p = database.withSession { implicit s :Session =>
      Post.insert(post)
    }
    Post(p, name, 4 ,new Date(),"",filename,Post.extraDataToJson(s"thumb=$thumb"))
  }
}
