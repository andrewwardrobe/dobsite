package controllers

import controllers.Application._
import jp.t2v.lab.play2.auth._
import jp.t2v.lab.play2.auth.AuthElement
import models.Blog

import models.UserRole.{Contributor, NormalUser}
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
    val id = database.withSession { implicit s =>
      Blog.insert(item)
    }
    Ok(""+id)
  }

  def submitBlogUpdate = StackAction(AuthorityKey -> Contributor) { implicit response =>
    val item = Blog.blogForm.bindFromRequest().get
    database.withSession { implicit s =>
      Blog.update(item)
    }
    Ok(""+item.id)
  }

}
