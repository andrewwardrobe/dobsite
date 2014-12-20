package controllers


import java.io._
import java.net.URLConnection
import java.nio.file.{Path, Files}
import java.text.SimpleDateFormat
import java.util.Calendar

import controllers.routes
import models._
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.json.Json._



object Application extends Controller {



  implicit val personFormat =  Json.format[Person]

  def index = Action {
    Ok(views.html.index(""))
  }

  def blogInput = Action {
    Ok(views.html.blogInput("",Blog.blogForm,-1))
  }

  def blogUpdate(id: Int) = Action {
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

  def upload = Action(parse.temporaryFile) { request =>
    request.body.moveTo(new File("/tmp/dob.jpg"))
    val baseDir = "public/images/uploaded"

    val is = new BufferedInputStream(new FileInputStream(request.body.file))
    val mimetype = URLConnection.guessContentTypeFromStream(is);
    is.close();

    val dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS")
    val df = dateFormat.format(Calendar.getInstance().getTime())

    val filename = baseDir + "/upload-"+ df + "." + mimetype.split("/")(1)
    request.body.moveTo(new File(filename))

    Ok(filename.replace("public","assets"))
  }

  def news = Action {
    Ok(views.html.news(""))
  }

  def hansUndJorg = Action {
    Ok(views.html.huj(""))
  }

  def discography = Action {
    Ok(views.html.discography(""))
  }

  def biography = Action { Ok(views.html.biography(""))}

  def biographyDetail(id: Int) = DBAction { implicit resp =>
    val bio = Biography.getById(id)
    Ok(views.html.biodetails("")(bio.head))}


  def insert = DBAction { implicit response =>
    val person = Person.personForm.bindFromRequest().get
    Person.insert(person)


    Redirect(routes.Application.index)
  }


  def javascriptRoutes = Action{
    implicit request =>

      Ok(Routes.javascriptRouter("jsRoutes")(
          routes.javascript.Application.submitBlog,
          routes.javascript.JsonApi.getPostById,
        routes.javascript.Application.submitBlogUpdate
        )
      ).as("text/javascript")
  }
  def biographies = TODO

}