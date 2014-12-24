package controllers

import controllers.Application._
import jp.t2v.lab.play2.auth._
import jp.t2v.lab.play2.auth.AuthElement
import models.Blog

import models.UserRole.NormalUser
import play.api.mvc.Controller
import play.api.db.DB
import play.api.db.slick.DBAction

import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import scala.slick.jdbc.JdbcBackend._


/**
 * Created by andrew on 23/12/14.
 */
object AuthApplication extends Controller with AuthElement with AuthConfigImpl {
  def leek = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    Ok(views.html.index(""))
  }

  def blogInput = StackAction(AuthorityKey -> NormalUser){  implicit request =>
    Ok(views.html.blogInput("",Blog.blogForm,-1))
  }

  def blogUpdate(id: Int) = StackAction(AuthorityKey -> NormalUser) {  implicit request =>
    Ok(views.html.blogInput("",Blog.blogForm,id))
  }

  def submitBlog = DBAction { implicit response =>
    val item = Blog.blogForm.bindFromRequest().get
    val id = Blog.insert(item)
    Ok(""+id)
  }

  def submitBlogUpdate = DBAction { implicit response =>
    val item = Blog.blogForm.bindFromRequest().get
    Blog.update(item)
    Ok(""+item.id)
  }

}
