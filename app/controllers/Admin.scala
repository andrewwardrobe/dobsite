package controllers

import com.daoostinboyeez.git.GitRepo
import controllers.Authorised._
import data.{ContentReloader, UserAccounts}
import jp.t2v.lab.play2.auth.AuthElement
import models.UserAccount
import models.UserRole.Administrator
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller

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

  def changeRole(name: String,role:String) = StackAction(AuthorityKey -> Administrator) {  implicit request =>

    database.withSession { implicit s =>
      val user = UserAccounts.findByName(name).get
      UserAccounts.changeRole(user, role)
    } match {
      case 0 => Ok("")
      case 1=> BadRequest("Unknown User Role")
    }
  }

  def changeEmail(name: String, email:String) = StackAction(AuthorityKey -> Administrator) { implicit request =>

    database.withSession { implicit s =>
      UserAccounts.getEmailCount(email) match {
        case 0 =>
          val user = UserAccounts.findByName(name).get
          UserAccounts.newEmail(user, email)
          Ok("Email Updated")
        case _ =>
          BadRequest("Email In Use")
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

  def changePassword = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val data: PassChange = changePassForm.bindFromRequest().get
    if (data.password != data.confirm) {
      BadRequest("Passwords Do Not Match")
    } else {
      database.withSession { implicit s =>
        val user = UserAccounts.findByName(data.userName).head
        UserAccounts.newPasswd(user, data.password)
      }
      Ok("Password Updated")
    }
  }
}
