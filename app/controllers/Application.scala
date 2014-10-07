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
    Ok(views.html.news(""))
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
  //Move this into a test function
  def leek = DBAction { implicit resp =>

    Person.insert(Person("Andrew","29"))
    Person.insert(Person("Jimmo","29"))
    Person.insert(Person("Billy","29"))
    Person.insert(Person("Smello","29"))

    Redirect(routes.Application.jsonFindAll)
  }

  //And this
  def leek2 = DBAction { implicit resp =>
    Ok(toJson(Person.getByName("Andrew")))
  }

  def jsonFindAll = DBAction { implicit response =>
    Ok(toJson(Person.get))

  }



  def insert = DBAction { implicit response =>
    val person = Person.personForm.bindFromRequest().get
    Person.insert(person)


    Redirect(routes.Application.index)
  }

  def jsonInsert = DBAction(parse.json) { implicit response =>
    response.request.body.validate[Person].map {pers =>
      Person.insert(pers)
      Ok(toJson(pers))
    } .getOrElse(BadRequest("Invalid JSon"))

  }


  def biographies = TODO

}