package controllers

import jp.t2v.lab.play2.auth.AuthConfig
import models.UserRole.{NormalUser, Administrator}
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
trait AuthConfigImpl extends AuthConfig {

  type Id = Int
  type User = UserAccount
  type Authority = UserRole
  val sessionTimeoutInSeconds: Int = 3600
  def database = Database.forDataSource(DB.getDataSource())

  val idTag: ClassTag[Id] = classTag[Id]

  def resolveUser(id: Id)(implicit ctx: ExecutionContext): Future[Option[User]] = { database.withSession{ implicit s =>
    Future.successful(UserAccount.findById(id))
  } }

  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    val uri = request.session.get("access_uri").getOrElse(routes.Application.index())
    Future.successful(Redirect(routes.Application.index))

  }

  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
    Future.successful(Redirect(routes.Application.index))

  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
    Future.successful(Redirect(routes.Auth.login))


  def authorizationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
    Future.successful(Forbidden("They are no one"))


  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = Future.successful {
    (user.role, authority) match {
      case (Administrator, _)       => true
      case (NormalUser, NormalUser) => true
      case _                        => false
    }
  }


  override lazy val cookieSecureOption: Boolean = play.api.Play.isProd(play.api.Play.current)


  override lazy val isTransientCookie: Boolean = false

}
