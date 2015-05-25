package controllers


import java.io._
import java.net.URLConnection
import java.nio.file.{Path, Files}
import java.text.SimpleDateFormat
import java.util.Calendar



import jp.t2v.lab.play2.auth.{OptionalAuthElement, AuthElement}
import models.UserRole.TrustedContributor
import models._
import org.apache.commons.io.IOUtils
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.libs.json.Json.parse
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.json.Json._


object Application extends Controller  with OptionalAuthElement with StandardAuthConfig{




  def index = StackAction { implicit request =>
    val maybeUser: Option[User] = loggedIn

    Ok(views.html.index(maybeUser))
  }

  def post(id :String) =  Action{ implicit request =>
   Ok(views.html.post("",id))
  }

  def playground =  Action{ implicit request =>
    Ok(views.html.playground(""))
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

  def news = Action {  implicit request =>
    Ok(views.html.contentList(routes.JsonApi.getContentByDate(ContentTypeMap.get("News")).url))
  }

  def gazthree = Action {  implicit request =>
    Ok(views.html.contentList(routes.JsonApi.getContentByDate(ContentTypeMap.get("Gaz Three")).url))
  }

  def hansUndJorg = Action {  implicit request =>
    Ok(views.html.huj(""))
  }

  def discography = Action { implicit request =>
    Ok(views.html.discography(""))
  }

  def biography = StackAction {  implicit request =>
    val maybeUser: Option[User] = loggedIn
    maybeUser match {
      case None => {Ok (views.html.biography ("", 0) )}
      case Some(user) => {
        user.hasPermission(TrustedContributor) match {
          case false => Ok (views.html.biography ("", 0) )
          case true => Ok (views.html.biography ("", 1) )
        }
      }
    }
  }






  def javascriptRoutes = Action{
    implicit request =>

      Ok(Routes.javascriptRouter("jsRoutes")(
          routes.javascript.Authorised.submitBlog,
          routes.javascript.JsonApi.getPostById,
          routes.javascript.JsonApi.getContentByUserLatestFirst,
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
        routes.javascript.AdminJsonApi.getUser
        )
      ).as("text/javascript")
  }
  def biographies = TODO

}