package controllers



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
    Ok(views.html.blogInput("",Blog.blogForm))
  }

  def submitBlog = DBAction { implicit response =>
    val item = Blog.blogForm.bindFromRequest().get
    Blog.insert(item)
    Ok(views.html.index(""))
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


  def biographies = TODO

}