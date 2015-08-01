package controllers

import java.util.{UUID, Date}

import com.daoostinboyeez.git.{GitRepo}
import com.daoostinboyeez.site.exceptions.AliasLimitReachedException
import controllers.Application._
import data.{UserAccounts, Profiles, Tags, Content}
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
import scala.slick.jdbc.JdbcBackend._



/**
 * Created by andrew on 23/12/14.
 */
object Authorised extends Controller with AuthElement with StandardAuthConfig {


  val repo = GitRepo.apply()

  def index = StackAction(AuthorityKey -> Contributor) { implicit request =>
    val user = loggedIn

    Ok(views.html.index(Some(user)))
  }

  def profile = StackAction(AuthorityKey -> InActiveUser) { implicit request =>
    database.withSession { implicit session =>
      val user = loggedIn
      val profile = Profiles.getByUserId(user.id)
      Ok(views.html.profile("", user, profile))
    }
  }

  def getEditables = StackAction(AuthorityKey -> InActiveUser) { implicit request =>
    val user = loggedIn
    val json = Json.obj("pages" -> Json.toJson(user.userRole.roles))
    Ok(json)
  }

  def newContent(contentType:String) = StackAction(AuthorityKey -> Contributor){  implicit request =>
    val maybeUser  =  loggedIn
    val typ = ContentTypeMap.get(contentType)
    Ok(views.html.editor("",ContentPost.blogForm,"-1",typ,Some(maybeUser)))
  }

  def blogInput = StackAction(AuthorityKey -> Contributor){  implicit request =>
    val maybeUser = loggedIn
    Ok(views.html.editor("",ContentPost.blogForm,"-1",1,Some(maybeUser)))
  }

  def blogUpdate(id: String) = StackAction(AuthorityKey -> Contributor) {  implicit request =>
    val maybeUser = loggedIn
    Ok(views.html.editor("",ContentPost.blogForm,id,1,Some(maybeUser)))
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

          val newItem = Content.save(item,repo,Some(user.id),tags)
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

  def addAlias(alias: String) = StackAction(AuthorityKey -> Contributor) { implicit request =>
    val user = loggedIn
    database.withSession { implicit session =>
      try {
        UserAccounts.addAlias(user, alias) match {
          case true => Ok(alias)
          case false => BadRequest("Alias already in use")
        }
      } catch {
        case ex: AliasLimitReachedException => BadRequest("Alias Limit Reached")
      }

    }
  }

  //TODO: Reactor this to use the save from content
  def submitBlogUpdate = StackAction(AuthorityKey -> Contributor) { implicit request =>
    val user = loggedIn
    ContentPost.blogForm.bindFromRequest() match {
      case s: Form[ContentPost] => {
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

          val newItem = Content.save(item, repo, Some(user.id), tags)
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
    val profile = Profiles.form.bindFromRequest().get
    val user = loggedIn
    if (profile.userId == user.id) {

        database.withSession { implicit session =>
          Profiles.update(profile)
          Ok("")
        }
      }
       else Forbidden("You do not have permission to perform this request")
    }


}
