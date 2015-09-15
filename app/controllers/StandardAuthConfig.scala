package controllers

import _root_.data.Users
import jp.t2v.lab.play2.auth.{CookieTokenAccessor, AuthConfig}
import models.UserRole._
import models.{UserRole, UserAccount}



import scala.concurrent.{ExecutionContext, Future}
import reflect.ClassTag
import reflect._
import play.api.mvc._
import play.api._
import play.api.mvc.Results._
import play.api.Play.current




/**
 * Created by andrew on 23/12/14.
 */
trait StandardAuthConfig extends AuthConfig {

  type Id = String
  type User = UserAccount
  type Authority = UserRole
  val sessionTimeoutInSeconds: Int = 3600


  val idTag: ClassTag[Id] = classTag[Id]

  def resolveUser(id: Id)(implicit ctx: ExecutionContext): Future[Option[User]] = {
    Users.findByName(id).map { users =>
      users.headOption
    }
  }

  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    val uri = request.session.get("access_uri").getOrElse(routes.Application.index()).toString()
    val req = request match {
      case r:Request[_] => r
    }
    Future.successful(Redirect(uri).withSession(request.session - "access_uri"))

  }



  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
    Future.successful(Redirect(routes.UserServices.signedout))

  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    Future.successful(Redirect(routes.UserServices.login).withSession("access_uri" -> request.uri))
  }


  def authorizationFailed(request: RequestHeader,user: User, authority: Option[Authority])(implicit ctx: ExecutionContext): Future[Result] = {
    Future.successful(Forbidden("They are no one"))
  }

  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = Future.successful {
    UserRole.roleHasAuthority(user.userRole,authority)
  }

  override lazy val tokenAccessor = new CookieTokenAccessor(
    /*
     * Whether use the secure option or not use it in the cookie.
     * Following code is default.
     */
    cookieSecureOption = play.api.Play.isProd(play.api.Play.current),
    cookieMaxAge       = Some(sessionTimeoutInSeconds)
  )


}
