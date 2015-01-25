package controllers

import java.util.Date

import com.daoostinboyeez.git.GitRepo
import controllers.Application._
import jp.t2v.lab.play2.auth._
import jp.t2v.lab.play2.auth.AuthElement
import models.{UserAccount, Blog}

import models.UserRole.{Administrator, Contributor, NormalUser}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import play.api.db.DB
import play.api.db.slick.DBAction

import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import scala.slick.jdbc.JdbcBackend._



/**
 * Created by andrew on 23/12/14.
 */
object Authorised extends Controller with AuthElement with AuthConfigImpl {




  def index = StackAction(AuthorityKey -> Contributor) { implicit request =>
    val user = loggedIn

    Ok(views.html.index(Some(user)))
  }

  def blogInput = StackAction(AuthorityKey -> Contributor){  implicit request =>
    Ok(views.html.blogInput("",Blog.blogForm,-1))
  }

  def blogUpdate(id: Int) = StackAction(AuthorityKey -> Contributor) {  implicit request =>
    Ok(views.html.blogInput("",Blog.blogForm,id))
  }


  def submitBlog = StackAction(AuthorityKey -> Contributor) { implicit response =>

    val item = Blog.blogForm.bindFromRequest().get
    val content = item.content
    val filename = genFileName

    val newItem = new Blog(item.id,item.title,item.postType,item.dateCreated,item.author,filename)
    GitRepo.newFile(filename,content)
    val id = database.withSession { implicit s =>
      Blog.insert(newItem)
    }
    Ok(""+id)
  }

  def genFileName = {
    val date = new Date()
    new String(""+date.getTime())
  }


  def submitBlogUpdate = StackAction(AuthorityKey -> Contributor) { implicit response =>
    val item = Blog.blogForm.bindFromRequest().get
    val content = item.content

    database.withSession { implicit s =>
      val filename = Blog.getById(item.id).head.content
      val newItem = new Blog(item.id,item.title,item.postType,item.dateCreated,item.author,filename)
      GitRepo.updateFile(filename,content)
      Blog.update(newItem)
    }
    Ok(""+item.id)
  }

}
