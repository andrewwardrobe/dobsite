package controllers

import controllers.Authorised._
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

  def admin = StackAction(AuthorityKey -> Administrator) {  implicit request =>
    Ok(views.html.admin(""))
  }


  def changeRole(name: String,role:String) = StackAction(AuthorityKey -> Administrator) {  implicit request =>

    database.withSession { implicit s =>
      val user = UserAccount.findByName(name).get
      UserAccount.changeRole(user, role)
    } match {
      case 0 => Ok("")
      case 1=> BadRequest("Unknown User Role")
    }
  }

  def changeEmail(name: String, email:String) = StackAction(AuthorityKey -> Administrator) { implicit request =>

    database.withSession { implicit s =>
      UserAccount.getEmailCount(email) match {
        case 0 =>
          val user = UserAccount.findByName(name).get
          UserAccount.newEmail(user, email)
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
        val user = UserAccount.findByName(data.userName).head
        UserAccount.newPasswd(user, data.password)
      }
      Ok("Password Updated")
    }
  }
}
