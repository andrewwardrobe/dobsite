package controllers

import controllers.Application._
import data.{UserProfiles, Profiles, UserAccounts}
import jp.t2v.lab.play2.auth.{OptionalAuthElement, LoginLogout}
import models.UserRole.InActiveUser
import models.{Profile, UserProfile, UserRoleMapping, UserAccount}
import play.api.data.Form
import play.api.data.validation.{Invalid, Valid, ValidationError, Constraint}
import play.api.mvc.{Security, Action, Controller}
import play.api.data.Form
import play.api.data.Forms._
import reactivemongo.bson.BSONObjectID

import scala.concurrent.{Await, Future}
import play.api.db.DB
import play.api.db.slick.DBAction

import scala.slick.jdbc.JdbcBackend._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.duration.DurationInt

/**
 * Created by andrew on 21/12/14.
 */
object UserServices extends Controller with LoginLogout with OptionalAuthElement with StandardAuthConfig {

  import models.JsonFormats._

  def loginForm(implicit session: Session) = {

      Form {
        mapping("email" -> text, "password" -> text)(UserAccounts.authenticate)(_.map(u => (u.email, "")))
          .verifying("Invalid email or password", result => result.isDefined)

    }
  }


  def login() = StackAction{  implicit request =>
    val maybeUser: Option[User] = loggedIn
    database.withSession { implicit session =>
      Ok(views.html.login(loginForm,maybeUser))
    }
  }

  def signup = StackAction{ implicit request =>
    val maybeUser: Option[User] = loggedIn
    Ok(views.html.signup(signUpForm,maybeUser))
  }

  def register = DBAction{ implicit request =>
    val newAccount = signUpForm.bindFromRequest().get
    //validate the user account
    import newAccount._
    val incAccount = newAccount.copy(role =  "InActiveUser")

      UserAccounts.create(incAccount)
      val newProfile = new Profile(BSONObjectID.generate,newAccount._id,"","", None)
      UserProfiles.insert(newProfile)
      Redirect(routes.UserServices.login)
  }



def signedout = StackAction { implicit request =>
  val maybeUser: Option[User] = loggedIn
  Ok(views.html.signout("",maybeUser)).removingFromSession("username")
}

  def signout = Action.async { implicit request =>
    gotoLogoutSucceeded
  }

  def authenticate = Action.async{ implicit request =>
    database.withSession { implicit session =>
      val submission = loginForm.bindFromRequest()
      submission.fold(
        formWithErrors => Future.successful(BadRequest(views.html.login(formWithErrors,None))),
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

        val count = Await.result(UserAccounts.getEmailCount(email),10 seconds)
        val errors = count match {
          case 0 => Nil
          case _ => Seq(ValidationError("Email Address is already in use"))
        }


      if(errors.isEmpty) {
        Valid
      }else{
        Invalid(errors)
      }
  })

  val userNameIsAvailable: Constraint[String] = Constraint("constraints.nameavailable")({
    name =>

        val available = Await.result(UserProfiles.aliasAvailable(name), 10 seconds)
      val errors = if (available) {
          Nil
        } else {
          Seq(ValidationError("Screen Name is already in use"))
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
      "role" -> ignored("InactiveUser"),
      "aliasLimit" -> optional(number)
    )(UserAccount.apply)(UserAccount.unapply _)
  }
}

