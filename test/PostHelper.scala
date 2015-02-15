import java.util.Date

import com.daoostinboyeez.git.GitRepo
import models.Blog
import play.api.db.DB

import play.api.db.slick.Config.driver.simple._

import play.api.Play.current
/**
 * Created by andrew on 14/02/15.
 */
object PostHelper {
  def database = Database.forDataSource(DB.getDataSource())

  val types = Map("News" -> 1, "Music" -> 2, "Gaz Three" -> 3)
  def createPost(title:String, author:String, content :String, typ: Int) = {
    val filename = GitRepo.createFile(content)
    val post = new Blog(1, title,typ,new Date(),author,filename)
    database.withSession { implicit s :Session =>
      Blog.insert(post)
    }
    post
  }
}
