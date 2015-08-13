package controllers

import controllers.Application._
import data.{Profiles, Users}
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




import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.duration.DurationInt

/**
 * Created by andrew on 21/12/14.
 */
object UserServices extends Controller with LoginLogout with OptionalAuthElement with StandardAuthConfig {

  import models.JsonFormats._

  def loginForm = {

      Form {
        mapping("email" -> text, "password" -> text)(Users.authenticate)(_.map(u => (u.email, "")))
          .verifying("Invalid email or password", result => result.isDefined)

    }
  }


  def login() = StackAction{  implicit request =>
    val maybeUser: Option[User] = loggedIn
      Ok(views.html.login(loginForm,maybeUser))
  }

  def signup = StackAction{ implicit request =>
    val maybeUser: Option[User] = loggedIn
    Ok(views.html.signup(signUpForm,maybeUser))
  }

  def register = StackAction{ implicit request =>
    val newAccount = signUpForm.bindFromRequest().get
    //validate the user account
    import newAccount._
    val incAccount = newAccount.copy(role =  "InActiveUser")

      Users.create(incAccount)
      val newProfile = new Profile(BSONObjectID.generate,newAccount._id,"","", None)
      Profiles.insert(newProfile)
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
    val submission = loginForm.bindFromRequest()
    submission.fold(
      formWithErrors => Future.successful(BadRequest(views.html.login(formWithErrors,None))),
      user => {
        gotoLoginSucceeded(user.get.name)
      }
    )
  }

  def checkName(name: String) = Action.async { implicit request =>
    Users.getUserNameCount(name).map { count =>
     Ok("" + count)
    }
  }

  def checkEmail(email: String) = Action.async{ implicit request =>
    Users.getEmailCount(email).map { count =>
      Ok(""+count)
    }



  }

  val userRoleMap = new UserRoleMapping()
  val emailIsAvailable: Constraint[String] = Constraint("constraints.accountavailable")({
    email =>

        val count = Await.result(Users.getEmailCount(email),10 seconds)
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

      val available = Await.result(aliasAvailable(name), 10 seconds)
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


  def aliasAvailable(alias:String) = {
    Users.getUserNameCount(alias).flatMap { count =>
      if (count > 0 ){
        Future(false)
      }else{
        Profiles.aliasAvailable(alias)
      }
    }
  }

  val signUpForm: Form[UserAccount] = Form {
    mapping(
      "id" -> ignored(BSONObjectID.generate),
      "email" -> email.verifying(emailIsAvailable),
      "password" -> nonEmptyText(minLength = 6, maxLength = 100),
      "name" -> text.verifying(userNameIsAvailable),
      "role" -> ignored("InactiveUser"),
      "aliasLimit" -> optional(number)
    )(UserAccount.apply)(UserAccount.unapply _)
  }
}

