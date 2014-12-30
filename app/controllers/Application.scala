package controllers


import java.io._
import java.net.URLConnection
import java.nio.file.{Path, Files}
import java.text.SimpleDateFormat
import java.util.Calendar


import jp.t2v.lab.play2.auth.{OptionalAuthElement, AuthElement}
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



object Application extends Controller  with OptionalAuthElement with AuthConfigImpl{



  implicit val personFormat =  Json.format[Person]

  def index = StackAction { implicit request =>
    val maybeUser: Option[User] = loggedIn

    Ok(views.html.index(maybeUser))
  }


  def upload = Action(parse.temporaryFile) { request =>

    val baseDir = "public/images/uploaded"

    val is = new BufferedInputStream(new FileInputStream(request.body.file))
    val mimetype = URLConnection.guessContentTypeFromStream(is)
    is.close()

    val dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS")
    val df = dateFormat.format(Calendar.getInstance().getTime())

    val filename = baseDir + "/upload-"+ df + "." + mimetype.split("/")(1)
    request.body.moveTo(new File(filename))

    Ok(filename.replace("public","assets"))
  }

  def news = Action {  implicit request =>
    Ok(views.html.news(""))
  }

  def hansUndJorg = Action {  implicit request =>
    Ok(views.html.huj(""))
  }

  def discography = Action { implicit request =>
    Ok(views.html.discography(""))
  }

  def biography = Action {  implicit request =>
    Ok(views.html.biography(""))}

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
          routes.javascript.Authorised.submitBlog,
          routes.javascript.JsonApi.getPostById,
        routes.javascript.Authorised.submitBlogUpdate,
        routes.javascript.UserServices.checkEmail,
        routes.javascript.UserServices.checkName
        )
      ).as("text/javascript")
  }
  def biographies = TODO

}