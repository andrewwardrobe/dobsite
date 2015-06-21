package controllers

import java.util.{UUID, Date}

import com.daoostinboyeez.git.{GitRepo}
import controllers.Application._
import data.{Profiles, Tags, Content}
import jp.t2v.lab.play2.auth._
import jp.t2v.lab.play2.auth.AuthElement
import models._

import models.UserRole.{InActiveUser, Administrator, Contributor, NormalUser}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.Controller
import play.api.db.DB
import play.api.db.slick.DBAction

import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import scala.slick.jdbc.JdbcBackend._



/**
 * Created by andrew on 23/12/14.
 */
object Authorised extends Controller with AuthElement with StandardAuthConfig {


  val repo = GitRepo.apply()

  def index = StackAction(AuthorityKey -> Contributor) { implicit request =>
    val user = loggedIn

    Ok(views.html.index(Some(user)))
  }

  def profile = StackAction(AuthorityKey -> InActiveUser) { implicit request =>
    database.withSession { implicit session =>
      val user = loggedIn
      val profile = Profiles.getByUserId(user.id)
      Ok(views.html.profile("", user, profile))
    }
  }

  def getEditables = StackAction(AuthorityKey -> InActiveUser) { implicit request =>
    val user = loggedIn
    val json = Json.obj("pages" -> Json.toJson(user.role.roles))
    Ok(json)
  }

  def newContent(contentType:String) = StackAction(AuthorityKey -> Contributor){  implicit request =>
    val maybeUser  =  loggedIn
    val typ = ContentTypeMap.get(contentType)
    Ok(views.html.editor("",ContentPost.blogForm,"-1",typ,Some(maybeUser)))
  }

  def blogInput = StackAction(AuthorityKey -> Contributor){  implicit request =>
    val maybeUser = loggedIn
    Ok(views.html.editor("",ContentPost.blogForm,"-1",1,Some(maybeUser)))
  }

  def blogUpdate(id: String) = StackAction(AuthorityKey -> Contributor) {  implicit request =>
    val maybeUser = loggedIn
    Ok(views.html.editor("",ContentPost.blogForm,id,1,Some(maybeUser)))
  }


  def submitBlog = StackAction(AuthorityKey -> Contributor) { implicit request =>
    val user = loggedIn
    ContentPost.blogForm.bindFromRequest() match {
      case s: Form[ContentPost] => {
        val item = s.get
        if(user.role.hasPermission(ContentTypeMap(item.postType))) {
          val content = item.content
          val filename = repo.genFileName
          val newItem = new ContentPost(UUID.randomUUID().toString(), item.title, item.postType, new Date(), item.author, filename, ContentPost.extraDataToJson(item.extraData),item.isDraft,Some(user.id))
          repo.doFile(filename, content, ContentMeta.makeCommitMsg("Created", newItem))
          val res = database.withSession {
            implicit s =>
              Content.insert(newItem)
              request.body.asFormUrlEncoded match {
                case None => {}
                case Some(formData) => {
                  formData.get("tags") match {
                    case Some(tagData) => {
                        tagData.foreach { tags =>
                          tags.split(",").foreach { str:String =>
                            val tag = Tags.create(str.trim)
                            Tags.link(newItem.id, tag.id)
                          }
                        }
                    }
                    case _ => {}
                  }
                }
                case _ => Logger.info("Matched Neither")
              }
              Ok(newItem.json)
          }
          res
        }else
          Unauthorized("You don't have the right privileges for "+ContentTypeMap(item.postType))
      }
      case _ => { BadRequest("Invalid Post Data")}
    }
  }

  def genFileName = {
    val date = new Date()
    new String(""+date.getTime())
  }


  def submitBlogUpdate = StackAction(AuthorityKey -> Contributor) { implicit request =>
    val item = ContentPost.blogForm.bindFromRequest().get
    val content = item.content
   //
    database.withSession { implicit s =>
      val post = Content.getById(item.id).head
      val filename = post.content

      //val tags = js \ "tags"
      val newItem = new ContentPost(item.id, item.title, item.postType, post.dateCreated, item.author, filename, ContentPost.extraDataToJson(item.extraData),item.isDraft,item.userId)
      repo.updateFile(filename,content, ContentMeta.makeCommitMsg("Updated",newItem))
      Content.update(newItem)
      request.body.asFormUrlEncoded match {
        case None => {Logger.info("Couldn;t get data from request")}
        case Some(formData) => {
          formData.get("tags") match {
            case Some(tagData) => {
              tagData.foreach { tags:String =>
                tags.split(",").foreach { str:String =>
                  val tag = Tags.create(str.trim)
                  Tags.link(newItem.id, tag.id)
                }
              }
            }
            case _ => {}
          }
        }
        case _ => Logger.info("Matched Niether")
      }
      //tags.toString.split(",").foreach{ str:String =>
      //  val tag = ContentTag.create(str.trim)
      //  PostToTag.link(newItem.id, tag.id)
     // }
      Ok(newItem.json)
    }

  }


  def updateProfile = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val profile = Profiles.form.bindFromRequest().get
    val user = loggedIn
    if (profile.userId == user.id) {

        database.withSession { implicit session =>
          Profiles.update(profile)
          Ok("")
        }
      }
       else Forbidden("You do not have permission to perform this request")
    }


}
