package controllers

import java.text.SimpleDateFormat
import java.util.{UUID, Date}

import com.daoostinboyeez.git.{GitRepo}
import com.daoostinboyeez.site.exceptions.AliasLimitReachedException
import controllers.Application._
import data._
import jp.t2v.lab.play2.auth._
import jp.t2v.lab.play2.auth.AuthElement
import models._

import models.UserRole.{InActiveUser, Administrator, Contributor, NormalUser}
import org.openqa.jetty.http.SecurityConstraint.Nobody
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsError, JsSuccess, JsObject, Json}
import play.api.libs.json.Json._
import play.api.mvc.{AnyContentAsFormUrlEncoded, AnyContentAsJson, Controller}
import play.api.db.DB


import play.mvc.BodyParser.FormUrlEncoded
import reactivemongo.bson.BSONObjectID
import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits.defaultContext


/**
 * Created by andrew on 23/12/14.
 */
object Authorised extends Controller with AuthElement with StandardAuthConfig {

 import models.JsonFormats._
 import models.Forms._

  val repo = GitRepo.apply()

  def index = StackAction(AuthorityKey -> Contributor) { implicit request =>
    val user = loggedIn

    Ok(views.html.index(Some(user)))
  }

  def profile = AsyncStack(AuthorityKey -> InActiveUser) { implicit request =>
      val user = loggedIn
      Profiles.findByUserId(user._id).map { profiles =>
          Ok(views.html.profile("", user, profiles.headOption))
      }

  }

  def getEditables = StackAction(AuthorityKey -> InActiveUser) { implicit request =>
    val user = loggedIn
    val json = Json.obj("pages" -> Json.toJson(user.userRole.roles))
    Ok(json)
  }

  def newContent(contentType:String) = AsyncStack(AuthorityKey -> Contributor){  implicit request =>
    val maybeUser  =  loggedIn
    val typ = ContentTypeMap.get(contentType)
    val postId = BSONObjectID.generate.stringify
    Logger.info("Noew Content "+postId)
    Profiles.findByUserId(maybeUser._id).map { profiles =>
      Ok(views.html.editor("",models.Forms.blogForm,postId,typ,Some(maybeUser),profiles.headOption,true))
    }

  }

  def blogInput = AsyncStack(AuthorityKey -> Contributor){  implicit request =>
    val maybeUser = loggedIn
    Profiles.findByUserId(maybeUser._id).map { profiles =>
      Ok(views.html.editor("",models.Forms.blogForm,BSONObjectID.generate.stringify,1,Some(maybeUser),profiles.headOption,true))
    }
  }

  def blogUpdate(id: String) = AsyncStack(AuthorityKey -> Contributor) {  implicit request =>
    val maybeUser = loggedIn
    Profiles.findByUserId(maybeUser._id).map { profiles =>
      Ok(views.html.editor("",models.Forms.blogForm,id,1,Some(maybeUser),profiles.headOption,false))
    }
  }


  def submitPost = AsyncStack(AuthorityKey -> Contributor) { implicit request =>
    val user = loggedIn

    request.body match {
      case form: AnyContentAsFormUrlEncoded =>
        request.body.asFormUrlEncoded.map({ data =>
          val userId = if (data("userId")(0) == "")
            None
          else
            Some(BSONObjectID(data("userId")(0)))

          val tags = if (data("tags")(0) == "")
            None
          else
            Some(data("tags")(0).split(",").toList)

          val postType = data("postType")(0).toInt
          val df = new SimpleDateFormat("yyyyMMddHHmmss")
          val date = df.parse(data("dateCreated")(0))
          val post = new Post(BSONObjectID(data("_id")(0)), data("title")(0), postType, date, data("author")(0), data("content")(0),
          data("extraData")(0), data("isDraft")(0).toBoolean, userId, tags)
          canEditPost(user,post).flatMap { perm =>
            perm match {
              case true =>
                Content.create(post, repo).map { res =>
                  Ok(toJson(post))
                }
              case _ =>
                Future {
                  Unauthorized("You don't have the right privileges for " + ContentTypeMap(post.postType))
                }
            }
          }
        }).getOrElse(Future {
          BadRequest("Invalid Post Data")
        })

      case json: AnyContentAsJson =>

        request.body.asJson.map({js => fromJson[Post](js) match {
          case JsSuccess(post, _) => {
            canEditPost(user,post).flatMap { perm =>
              perm match {
                case true =>
                  Content.create(post, repo).map { res =>
                    Ok(toJson(post))
                  }
                case _ =>
                  Future {
                    Unauthorized("You don't have the right privileges for " + ContentTypeMap(post.postType))
                  }
              }
            }
          }
          case JsError(err) => Future { BadRequest("Error")}
          }
        }).getOrElse(Future {
          BadRequest
        })
    }
  }

  def genFileName = {
    val date = new Date()
    new String(""+date.getTime)
  }

  def addAlias(alias: String) = AsyncStack(AuthorityKey -> Contributor) { implicit request =>
    val user = loggedIn
      try {
        Profiles.findByUserId(user._id).flatMap{ profile =>
          val aliasLimit = user.userRole.aliasLimit
          val aliasCount = profile.head.aliases.getOrElse(Nil).length
          aliasCount match {
            case x if x < aliasLimit =>
              Profiles.addAlias(profile.head, alias).map { result =>
                result.ok match {
                  case true => Ok(alias)
                  case false => BadRequest("Could not  add Alias")
                }
              }
            case _ => Future { BadRequest("Alias Limit Reached") }
          }
        }
      } catch {
        case ex: AliasLimitReachedException => Future.successful(BadRequest("Alias Limit Reached"))
      }
  }

  def canEditPost(user: User, post: Post) = {

    if (user.userRole == Administrator)
      Future(true)
    else {
      Content.findById(post.id).map { posts =>
        posts.headOption match {
          case None => post.userId match {
            case None => user.userRole.hasPermission (ContentTypeMap (post.postType) )
            case Some (id) => user._id == id && user.userRole.hasPermission (ContentTypeMap (post.postType) )
          }
          case Some(savedPost) => savedPost.userId match {
            case None => user.userRole.hasPermission (ContentTypeMap (post.postType) )
            case Some (id) => user._id == id && user.userRole.hasPermission (ContentTypeMap (post.postType) )
          }
        }
      }
    }
  }

  def submitBlogUpdate = AsyncStack(AuthorityKey -> Contributor) { implicit request =>

    //Todo Do jsoup white listing on the content and title before saving
    val user = loggedIn
    request.body match {
      case form: AnyContentAsFormUrlEncoded =>
        request.body.asFormUrlEncoded.map({ data =>
          val userId = if (data("userId")(0) == "")
            None
          else
            Some(BSONObjectID(data("userId")(0)))

          val tags = if (data("tags")(0) == "")
            None
          else
            Some(data("tags")(0).split(",").toList)

          val postType = data("postType")(0).toInt
          val df = new SimpleDateFormat("yyyyMMddHHmmss")
          val date = df.parse(data("dateCreated")(0))
          val post = new Post(BSONObjectID(data("_id")(0)), data("title")(0), postType, date, data("author")(0), data("content")(0),
            data("extraData")(0), data("isDraft")(0).toBoolean, userId, tags)
          canEditPost(user,post).flatMap { perm =>
            perm match {
              case true =>
                Content.save(post, repo).map { res =>
                  Ok(toJson(post))
                }
              case _ =>
                Future {
                  Unauthorized("You don't have the right privileges for " + ContentTypeMap(post.postType))
                }
            }
          }
        }).getOrElse(Future {
          BadRequest("Invalid Post Data")
        })

      case json: AnyContentAsJson =>
        request.body.asJson.map(js => fromJson[Post](js) match {
          case JsSuccess(post, _) => {
            canEditPost(user,post).flatMap { perm =>
              perm match {
                case true =>
                  Content.save(post, repo).map { res =>
                    Ok(toJson(post))
                  }
                case _ =>
                  Future {
                    Unauthorized("You don't have the right privileges for " + ContentTypeMap(post.postType))
                  }
              }
            }
          }
          case JsError(err) => Future {
            BadRequest("Error")
          }
        }).getOrElse(Future {
          BadRequest
        })
    }
  }


  def updateProfile = AsyncStack(AuthorityKey -> NormalUser) { implicit request =>
    val user = loggedIn
    def doUpdate(profile:Profile) = {
      if (profile.userId.stringify == user._id.stringify) {
        Profiles.update(profile._id.stringify, profile).map{res =>
             Ok("")
          }
        }
       else
        Future{
          Forbidden("You do not have permission to perform this request")
        }
    }
    request.body match {
      case form: AnyContentAsFormUrlEncoded =>
        val profile = profileForm.bindFromRequest().get
        doUpdate(profile)
      case json: JsObject =>
        request.body.asJson.map(js => fromJson[Profile](js) match {
          case JsSuccess(profile,_) => doUpdate(profile)
        }).getOrElse(Future{
          BadRequest
        })
      case json: AnyContentAsJson =>
        request.body.asJson.map(js => fromJson[Profile](js) match {
          case JsSuccess(profile,_) => doUpdate(profile)
          case JsError(err) => Future { BadRequest("Error") }
        }).getOrElse(Future{
          BadRequest
        })
    }
  }


}
