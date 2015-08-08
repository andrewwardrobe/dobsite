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
import play.api.libs.json.Json._
import play.api.mvc.Controller
import play.api.db.DB
import play.api.db.slick.DBAction

import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import reactivemongo.bson.BSONObjectID
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
      Ok(views.html.editor("",models.Forms.blogForm,BSONObjectID.generate.stringify,typ,Some(maybeUser),profiles.headOption,true))
    }

  }

  def blogInput = AsyncStack(AuthorityKey -> Contributor){  implicit request =>
    val maybeUser = loggedIn
    UserProfiles.getByUserId(maybeUser._id).map { profiles =>
      Ok(views.html.editor("",models.Forms.blogForm,BSONObjectID.generate.stringify,1,Some(maybeUser),profiles.headOption,true))
    }
  }

  def blogUpdate(id: String) = AsyncStack(AuthorityKey -> Contributor) {  implicit request =>
    val maybeUser = loggedIn
    UserProfiles.getByUserId(maybeUser._id).map { profiles =>
      Ok(views.html.editor("",models.Forms.blogForm,BSONObjectID.generate.stringify,1,Some(maybeUser),profiles.headOption,false))
    }
  }


  def submitPost = AsyncStack(AuthorityKey -> Contributor) { implicit request =>
    val user = loggedIn
    models.Forms.blogForm.bindFromRequest() match {
      case s: Form[MongoPost] => {
        val item = s.get
        if(user.userRole.hasPermission(ContentTypeMap(item.postType))) {
          val postTags : Option[Seq[String]] = request.body.asFormUrlEncoded match {
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
          Content.create(item.copy(tags = postTags),repo).map { res =>
            Ok(toJson(item))
          }

        }else
          Future {
            Unauthorized("You don't have the right privileges for "+ContentTypeMap(item.postType))
          }
      }
      case _ => { Future {
        BadRequest("Invalid Post Data")
        }
      }
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
  def submitBlogUpdate = AsyncStack(AuthorityKey -> Contributor) { implicit request =>

    //Todo Do jsoup white listing on the content and title before saving
    val user = loggedIn
    models.Forms.blogForm.bindFromRequest() match {
      case s: Form[MongoPost] => {
        val item = s.get
        if (user.userRole.hasPermission(ContentTypeMap(item.postType))) {
          val postTags: Option[Seq[String]] = request.body.asFormUrlEncoded match {
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

         Content.save(item.copy(tags = postTags),repo).map { res =>
            Ok(toJson(item))
          }


        } else
          Future{
            Unauthorized("You don't have the right privileges for " + ContentTypeMap(item.postType))
          }
      }
      case _ => {
        Future{
          BadRequest("Invalid Post Data")
        }
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
