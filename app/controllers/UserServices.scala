package controllers

import jp.t2v.lab.play2.auth.LoginLogout
import models.UserAccount
import play.api.data.Form
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
object UserServices extends Controller with LoginLogout with AuthConfigImpl {

  var loginForm = getForm

  def getForm = {
    database.withSession { implicit session =>
      Form {
        mapping("email" -> email, "password" -> text)(UserAccount.authenticate)(_.map(u => (u.email, "")))
          .verifying("Invalid email or password", result => result.isDefined)
      }
    }
  }


  def login() = Action{  implicit request =>
    Ok(views.html.login(loginForm))
  }

  def signup = Action{ implicit request =>
    Ok(views.html.signup(UserAccount.signUpForm))
  }

  def register = DBAction{ implicit request =>
    val newAccount = UserAccount.signUpForm.bindFromRequest().get
    database.withSession { implicit session =>
      UserAccount.create(newAccount)
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
    val submission = loginForm.bindFromRequest()
    submission.fold(
      formWithErrors => Future.successful(BadRequest(views.html.login(formWithErrors))),
      user =>{
        gotoLoginSucceeded(user.get.id)
      }
    )
  }

  def checkName(name: String) = Action{ implicit request =>
    val count = database.withSession { implicit s =>
      UserAccount.getUserNameCount(name)
    }
    Ok(""+count)
  }

  def checkEmail(email: String) = Action{ implicit request =>
    val count = database.withSession { implicit s =>
      UserAccount.getEmailCount(email)
    }
    Ok(""+count)
  }
}
