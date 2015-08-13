package controllers

import com.daoostinboyeez.git.GitRepo
import controllers.Authorised._
import data.{ContentReloader, Users}
import jp.t2v.lab.play2.auth.AuthElement
import models.{UserRole, UserAccount}
import models.UserRole.{InvalidUser, Administrator}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

/**
 * Created by andrew on 25/01/15.
 */
object Admin extends Controller with AuthElement with StandardAuthConfig{

  def reloadFromRepo = StackAction(AuthorityKey -> Administrator) {  implicit request =>
    val reloader = new ContentReloader(GitRepo.apply())
    reloader.reload
    Ok("Done")
  }

  def admin = StackAction(AuthorityKey -> Administrator) {  implicit request =>
    val user = loggedIn
    Ok(views.html.admin("",user))
  }

  def bulkUploader = StackAction(AuthorityKey -> Administrator) {  implicit request =>
    val user = loggedIn
    Ok(views.html.bulkuploader("",user))
  }

  def changeRole(name: String,role:String) = AsyncStack(AuthorityKey -> Administrator) {  implicit request =>
    Logger.info(s"User Name = ${name}")
    Users.findByName(name).map { users =>
      users.headOption match {
        case None => BadRequest("Unknown User")
        case Some(user) => {
          UserRole.valueOf(role) match {
            case InvalidUser => BadRequest("Unknown User Role")
            case _ => {
              Users.changeRole(user, role)
              Ok("User Role changed")
            }
          }
        }
      }
    }
  }

  def changeEmail(name: String, email:String) = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    Users.findByName(name).map { result =>
      result.headOption match {
        case Some(user) => {
          Users.newEmail(user, email)
          Ok("Email Updated")
        }
        case None => BadRequest("Account Not Found")
      }
    }
  }

  case class PassChange(userName:String, password:String, confirm:String)

  val changePassForm: Form[PassChange] = Form {
    mapping(
      "userName" -> text,
      "password" -> text,
      "confirm" ->text
    )(PassChange.apply)(PassChange.unapply)
  }

  def changePassword = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    val data: PassChange = changePassForm.bindFromRequest().get
    if (data.password != data.confirm) {
      Future.successful(BadRequest("Passwords Do Not Match"))
    } else {
      Users.findByName(data.userName).map { result =>
        result.headOption match {
          case Some(user) => {
            Users.newPasswd(user,data.password)
            Ok("Password Updated")
          }
          case None =>
            BadRequest("User Account Not Found")
        }
      }
    }
  }
}
