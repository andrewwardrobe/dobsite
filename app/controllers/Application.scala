package controllers


import java.io._
import java.net.URLConnection
import java.nio.file.{Path, Files}
import java.text.SimpleDateFormat
import java.util.{Date, Calendar}


import _root_.data.Content
import jp.t2v.lab.play2.auth.{OptionalAuthElement, AuthElement}
import models.UserRole.TrustedContributor
import models._
import org.apache.commons.io.IOUtils
import org.h2.mvstore.MVMap.MapBuilder
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.libs.json.Json.parse
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json.{JsObject, Json}
import play.api.libs.json.Json._

import scala.collection.immutable.HashMap
import scala.collection.mutable
import scala.util.Try
import play.api.libs.json.Json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object Application extends Controller  with OptionalAuthElement with StandardAuthConfig{

  import models.JsonFormats._
  lazy val numPosts = Play.configuration.getInt("blogroll.numposts").getOrElse(5)


  def about = StackAction { implicit request =>
    val maybeUser: Option[User] = loggedIn

    Ok(views.html.about("",maybeUser))
  }

  def index = StackAction { implicit request =>
    val maybeUser: Option[User] = loggedIn

    Ok(views.html.index(maybeUser))
  }

  def post(id :String) =  AsyncStack{ implicit request =>
    val maybeUser: Option[User] = loggedIn
    Content.getById(id).map { posts =>
      posts.headOption match {
        case Some(post) =>{
          val extraData = Try(Json.parse(post.extraData).as[Map[String,String]]).getOrElse(Map())
          Ok(views.html.post(post, extraData, maybeUser))
        }
        case None => NotFound("Could Not Find Post") //Todo custom page for 404 and bad request
      }
    }
  }

  def playground =  StackAction{ implicit request =>
    val maybeUser: Option[User] = loggedIn
    Ok(views.html.playground("",maybeUser))
  }

  def uploadedImage(file:String) = Action{ implicit response =>
    val image = try{
      IOUtils.toByteArray(new FileInputStream(new File("public/images/uploaded/"+file)));
    }catch {
      case ex: Exception=> {null}
    }
    if(image == null){
      NotFound("")
    }else{
      Ok(image).as("image")
    }


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

  def blog = AsyncStack { implicit request =>
    val maybeUser: Option[User] = loggedIn

    val query = Json.obj( //Todo make queries object holding common queries  when you refactor
      "typeId" -> ContentTypeMap.get("Blog")
    )
    Content.find(query,Json.obj("dateCreated" -> -1 )).map { posts =>
      Ok(views.html.blog("all", maybeUser, posts))
    }
  }

  def author(author: String) = StackAction { implicit request =>
    val maybeUser: Option[User] = loggedIn
    val posts = database.withSession { implicit session =>
      Content.getLiveContentByAuthorLatestFirst(author, ContentTypeMap.get("Blog"), new Date(), numPosts)
    }
    Ok(views.html.blog(s"author:${author}", maybeUser, posts))

  }

  def gazthree = StackAction {  implicit request =>
    val maybeUser: Option[User] = loggedIn
    val posts = database.withSession { implicit session =>
      Content.getByDate(ContentTypeMap.get("Blog"), 1)
    }
    Ok(views.html.blog(routes.JsonApi.getContentByDate(ContentTypeMap.get("Blog")).url, maybeUser, posts))
  }

  def hansUndJorg = StackAction {  implicit request =>
    val maybeUser: Option[User] = loggedIn
    Ok(views.html.huj("",maybeUser))
  }

  def discography = StackAction { implicit request =>
    val maybeUser: Option[User] = loggedIn
    Ok(views.html.discography("",maybeUser))
  }

  def biography = StackAction {  implicit request =>
    val maybeUser: Option[User] = loggedIn
    maybeUser match {
      case None => {Ok (views.html.biography ("", 0,maybeUser) )}
      case Some(user) => {
        user.hasPermission(TrustedContributor) match {
          case false => Ok (views.html.biography ("", 0,maybeUser) )
          case true => Ok (views.html.biography ("", 1,maybeUser) )
        }
      }
    }
  }






  def javascriptRoutes = Action{
    implicit request =>

      Ok(Routes.javascriptRouter("jsRoutes")(
          routes.javascript.Authorised.submitPost,
          routes.javascript.JsonApi.getPostById,
          routes.javascript.JsonApi.getContentByUserLatestFirst,
          routes.javascript.JsonApi.getDraftsByUserLatestFirst,
          routes.javascript.JsonApi.getRandomPosts,
        routes.javascript.JsonApi.getPostRevisionById,
        routes.javascript.JsonApi.getContentTags,
        routes.javascript.JsonApi.getRevisionsWithDates,
        routes.javascript.Authorised.submitBlogUpdate,
        routes.javascript.Authorised.newContent,
        routes.javascript.Authorised.updateProfile,
        routes.javascript.Admin.changeRole,
        routes.javascript.UserServices.checkEmail,
        routes.javascript.UserServices.checkName,
        routes.javascript.Admin.changePassword,
        routes.javascript.Admin.changeEmail,
        routes.javascript.AdminJsonApi.getUsers,
        routes.javascript.AdminJsonApi.getUser,
        routes.javascript.AdminJsonApi.insertDiscographies,
        routes.javascript.Authorised.addAlias,
        routes.javascript.JsonApi.getContentByDateStart,
        routes.javascript.JsonApi.getContentByAuthorDateStart
        )
      ).as("text/javascript")
  }
  def biographies = TODO

}