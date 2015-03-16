package test.helpers

import java.util.Date

import com.daoostinboyeez.git.GitRepo
import models.Post
import play.api.Play.current
import play.api.db.DB

import scala.slick.jdbc.JdbcBackend._
/**
 * Created by andrew on 14/02/15.
 */
object PostHelper {
  val repo = GitRepo.apply()
  def database = Database.forDataSource(DB.getDataSource())

  val types = Map("News" -> 1, "Music" -> 2, "Gaz Three" -> 3)
  def createPost(title:String, author:String, content :String, typ: Int) = {
    val filename = repo.createFile(content)
    val post = new Post(1, title,typ,new Date(),author,filename)
    database.withSession { implicit s :Session =>
      Post.insert(post)
    }
    post
  }
}
