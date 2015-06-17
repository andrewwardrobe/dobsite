package controllers

import data.{Profiles, UserAccounts}
import jp.t2v.lab.play2.auth.LoginLogout
import models.UserRole.InActiveUser
import models.{UserProfile, UserRoleMapping, UserAccount}
import play.api.data.Form
import play.api.data.validation.{Invalid, Valid, ValidationError, Constraint}
import play.api.mvc.{Security, Action, Controller}
import play.api.data.Form
import play.api.data.Forms._

import scala.concurrent.{Future}
import play.api.db.DB
import play.api.db.slick.DBAction

import scala.slick.jdbc.JdbcBackend._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * Created by andrew on 21/12/14.
 */
object UserServices extends Controller with LoginLogout with StandardAuthConfig {

  def loginForm(implicit session: Session) = {

      Form {
        mapping("email" -> text, "password" -> text)(UserAccounts.authenticate)(_.map(u => (u.email, "")))
          .verifying("Invalid email or password", result => result.isDefined)

    }
  }


  def login() = Action{  implicit request =>
    database.withSession { implicit session =>
      Ok(views.html.login(loginForm))
    }
  }

  def signup = Action{ implicit request =>
    Ok(views.html.signup(signUpForm))
  }

  def register = DBAction{ implicit request =>
    val newAccount = signUpForm.bindFromRequest().get
    //validate the user account
    import newAccount._
    val incAccount = new UserAccount(id, email, password, name, InActiveUser)
    database.withSession { implicit session =>
      val insertId = UserAccounts.create(incAccount)
      val newProfile = new UserProfile(0,insertId,"","")
      Profiles.create(newProfile)
    }
    Redirect(routes.UserServices.login)
  }



def signedout = Action { implicit request =>
  Ok(views.html.signout("")).removingFromSession("username")
}

  def signout = Action.async { implicit request =>
    gotoLogoutSucceeded
  }

  def authenticate = Action.async { implicit request =>
    database.withSession { implicit session =>
      val submission = loginForm.bindFromRequest()
      submission.fold(
        formWithErrors => Future.successful(BadRequest(views.html.login(formWithErrors))),
        user => {
          gotoLoginSucceeded(user.get.name)
        }
      )
    }
  }

  def checkName(name: String) = Action{ implicit request =>
    val count = database.withSession { implicit s =>
      UserAccounts.getUserNameCount(name)
    }
    Ok(""+count)
  }

  def checkEmail(email: String) = Action{ implicit request =>
    val count = database.withSession { implicit s =>
      UserAccounts.getEmailCount(email)
    }
    Ok(""+count)
  }

  val userRoleMap = new UserRoleMapping()
  val emailIsAvailable: Constraint[String] = Constraint("constraints.accountavailable")({
    email =>
      val errors = database.withSession { implicit session =>
        UserAccounts.getEmailCount(email) match {
          case 0 => Nil
          case _ => Seq(ValidationError("Email Address is already in use"))
        }
      }

      if(errors.isEmpty) {
        Valid
      }else{
        Invalid(errors)
      }
  })

  val userNameIsAvailable: Constraint[String] = Constraint("constraints.nameavailable")({
    name =>
      val errors = database.withSession { implicit session =>
        UserAccounts.getUserNameCount(name) match {
          case 0 => Nil
          case _ => Seq(ValidationError("Screen Name is already in use"))
        }
      }
      if(errors.isEmpty) {
        Valid
      }else{
        Invalid(errors)
      }
  })



  val signUpForm: Form[UserAccount] = Form {
    mapping(
      "id" -> number,
      "email" -> email.verifying(emailIsAvailable),
      "password" -> nonEmptyText(minLength = 6, maxLength = 100),
      "name" -> text.verifying(userNameIsAvailable),
      "role" -> userRoleMap

    )(UserAccount.apply)(UserAccount.unapply _)
  }
}

