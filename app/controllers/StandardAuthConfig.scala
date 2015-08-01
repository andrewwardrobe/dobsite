package controllers

import _root_.data.UserAccounts
import jp.t2v.lab.play2.auth.AuthConfig
import models.UserRole._
import models.{UserRole, UserAccount}
import play.api.db.DB
import play.api.db.slick.DBAction


import scala.concurrent.{ExecutionContext, Future}
import reflect.ClassTag
import reflect._
import play.api.mvc._
import play.api._
import play.api.mvc.Results._
import play.api.Play.current

import scala.slick.jdbc.JdbcBackend._


/**
 * Created by andrew on 23/12/14.
 */
trait StandardAuthConfig extends AuthConfig {

  type Id = String
  type User = UserAccount
  type Authority = UserRole
  val sessionTimeoutInSeconds: Int = 3600
  def database = Database.forDataSource(DB.getDataSource())

  val idTag: ClassTag[Id] = classTag[Id]

  def resolveUser(id: Id)(implicit ctx: ExecutionContext): Future[Option[User]] = { database.withSession{ implicit s =>
    Future.successful(UserAccounts.findByName(id))
  } }

  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    val uri = request.session.get("access_uri").getOrElse(routes.Application.index()).toString()
    val req = request match {
      case r:Request[_] => r
    }
    val email = req.body.asInstanceOf[AnyContentAsFormUrlEncoded].data("email")(0)
    val user = database.withSession { implicit s =>
      email.contains("@") match {
        case true =>
          UserAccounts.getUserName(email).get
        case false =>
          email
      }
    }
    Future.successful(Redirect(uri).withSession(request.session - "access_uri").withSession("username" -> user))

  }



  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
    Future.successful(Redirect(routes.UserServices.signedout))

  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    Future.successful(Redirect(routes.UserServices.login).withSession("access_uri" -> request.uri))
  }


  def authorizationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    Future.successful(Forbidden("They are no one"))
  }

  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = Future.successful {
    UserRole.roleHasAuthority(user.userRole,authority)
  }

  override lazy val cookieSecureOption: Boolean = play.api.Play.isProd(play.api.Play.current)


  override lazy val isTransientCookie: Boolean = false

}
