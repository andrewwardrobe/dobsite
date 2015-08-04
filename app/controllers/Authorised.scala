package controllers

import java.util.{UUID, Date}

import com.daoostinboyeez.git.{GitRepo}
import com.daoostinboyeez.site.exceptions.AliasLimitReachedException
import controllers.Application._
import data._
import jp.t2v.lab.play2.auth._
import jp.t2v.lab.play2.auth.AuthElement
import models._

import models.UserRole.{InActiveUser, Administrator, Contributor, NormalUser}
import org.openqa.jetty.http.SecurityConstraint.Nobody
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.Controller
import play.api.db.DB
import play.api.db.slick.DBAction

import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import scala.concurrent.Future
import scala.slick.jdbc.JdbcBackend._
import play.api.libs.concurrent.Execution.Implicits.defaultContext


/**
 * Created by andrew on 23/12/14.
 */
object Authorised extends Controller with AuthElement with StandardAuthConfig {

 import models.JsonFormats._
 import models.Forms._

  val repo = GitRepo.apply()

  def index = StackAction(AuthorityKey -> Contributor) { implicit request =>
    val user = loggedIn

    Ok(views.html.index(Some(user)))
  }

  def profile = AsyncStack(AuthorityKey -> InActiveUser) { implicit request =>
      val user = loggedIn
      UserProfiles.getByUserId(user._id).map { profiles =>
          Ok(views.html.profile("", user, profiles.headOption))
      }

  }

  def getEditables = StackAction(AuthorityKey -> InActiveUser) { implicit request =>
    val user = loggedIn
    val json = Json.obj("pages" -> Json.toJson(user.userRole.roles))
    Ok(json)
  }

  def newContent(contentType:String) = AsyncStack(AuthorityKey -> Contributor){  implicit request =>
    val maybeUser  =  loggedIn
    val typ = ContentTypeMap.get(contentType)
    UserProfiles.getByUserId(maybeUser._id).map { profiles =>
      Ok(views.html.editor("",ContentPost.blogForm,"-1",typ,Some(maybeUser),profiles.headOption))
    }

  }

  def blogInput = AsyncStack(AuthorityKey -> Contributor){  implicit request =>
    val maybeUser = loggedIn
    UserProfiles.getByUserId(maybeUser._id).map { profiles =>
      Ok(views.html.editor("",ContentPost.blogForm,"-1",1,Some(maybeUser),profiles.headOption))
    }
  }

  def blogUpdate(id: String) = AsyncStack(AuthorityKey -> Contributor) {  implicit request =>
    val maybeUser = loggedIn
    UserProfiles.getByUserId(maybeUser._id).map { profiles =>
      Ok(views.html.editor("",ContentPost.blogForm,"-1",1,Some(maybeUser),profiles.headOption))
    }
  }


  def submitPost = StackAction(AuthorityKey -> Contributor) { implicit request =>
    val user = loggedIn
    ContentPost.blogForm.bindFromRequest() match {
      case s: Form[ContentPost] => {
        val item = s.get
        if(user.userRole.hasPermission(ContentTypeMap(item.postType))) {
          val tags : Option[Seq[String]] = request.body.asFormUrlEncoded match {
            case None => { None }
            case Some(formData) => {
              formData.get("tags") match {
                case Some(tagData) => {
                  Some(tagData.toList)
                }
                case _ => {None}
              }
            }
            case _ => None
          }

//          val newItem = Content.save(item,repo,Some(user._id),tags) change this back later
          val newItem = Content.save(item,repo,None,tags)
          Ok(newItem.json)

        }else
          Unauthorized("You don't have the right privileges for "+ContentTypeMap(item.postType))
      }
      case _ => { BadRequest("Invalid Post Data")}
    }
  }

  def genFileName = {
    val date = new Date()
    new String(""+date.getTime)
  }

  def addAlias(alias: String) = AsyncStack(AuthorityKey -> Contributor) { implicit request =>
    val user = loggedIn
      try {
        UserProfiles.addAlias(user, alias).map { result =>
          Ok(alias)
        }
      } catch {
        case ex: AliasLimitReachedException => Future.successful(BadRequest("Alias Limit Reached"))
      }
  }
  //TODO: Reactor this to use the save from content
  def submitBlogUpdate = StackAction(AuthorityKey -> Contributor) { implicit request =>

    //Todo Do jsoup white listing on the content and title before saving
    val user = loggedIn
    ContentPost.blogForm.bindFromRequest() match {
      case s: Form[MongoPost] => {
        val item = s.get
        if (user.userRole.hasPermission(ContentTypeMap(item.postType))) {
          val tags: Option[Seq[String]] = request.body.asFormUrlEncoded match {
            case None => {
              None
            }
            case Some(formData) => {
              formData.get("tags") match {
                case Some(tagData) => {
                  Some(tagData.toList)
                }
                case _ => {
                  None
                }
              }
            }
            case _ => None
          }

         //put this back  val newItem = Content.save(item, repo, Some(user._id), tags)//
          val newItem = Content.save(item, repo, None, tags)
          Ok(newItem.json)

        } else
          Unauthorized("You don't have the right privileges for " + ContentTypeMap(item.postType))
      }
      case _ => {
        BadRequest("Invalid Post Data")
      }
    }

  }


  def updateProfile = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val profile = profileForm.bindFromRequest().get
    val user = loggedIn
    if (profile.userId == user._id) {
          UserProfiles.update(profile._id.stringify,profile)
          Ok("")
    } else
      Forbidden("You do not have permission to perform this request")
  }


}
