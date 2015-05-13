package controllers

import java.util.{UUID, Date}

import com.daoostinboyeez.git.{GitRepo}
import controllers.Application._
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

  def getEditables = StackAction(AuthorityKey -> InActiveUser) { implicit request =>
    val user = loggedIn
    val json = Json.obj("pages" -> Json.toJson(user.role.roles))
    Ok(json)
  }

  def newContent(contentType:String) = StackAction(AuthorityKey -> Contributor){  implicit request =>
    val typ = PostTypeMap.get(contentType)
    Ok(views.html.editor("",Post.blogForm,"-1",typ))
  }

  def blogInput = StackAction(AuthorityKey -> Contributor){  implicit request =>
    Ok(views.html.editor("",Post.blogForm,"-1"))
  }

  def blogUpdate(id: String) = StackAction(AuthorityKey -> Contributor) {  implicit request =>

    Ok(views.html.editor("",Post.blogForm,id))
  }


  def submitBlog = StackAction(AuthorityKey -> Contributor) { implicit request =>
    val user = loggedIn
    Post.blogForm.bindFromRequest() match {
      case s: Form[Post] => {
        val item = s.get
        if(user.role.hasPermission(PostTypeMap(item.postType))) {
          val content = item.content
          val filename = repo.genFileName
          val newItem = new Post(UUID.randomUUID().toString(), item.title, item.postType, new Date(), item.author, filename, Post.extraDataToJson(item.extraData),item.isDraft)
          repo.doFile(filename, content, PostMeta.makeCommitMsg("Created", newItem))
          val res = database.withSession {
            implicit s =>
              Post.insert(newItem)
              request.body.asFormUrlEncoded match {
                case None => {}
                case Some(formData) => {
                  formData.get("tags") match {
                    case Some(tagData) => {
                        tagData.foreach { tags =>
                          tags.split(",").foreach { str:String =>
                            val tag = ContentTag.create(str.trim)
                            PostToTag.link(newItem.id, tag.id)
                          }
                        }
                    }
                  }
                }
                case _ => Logger.info("Matched Niether")
              }
              Ok(newItem.json)
          }
          res
        }else
          Unauthorized("You don't have the right privileges for "+PostTypeMap(item.postType))
      }
      case _ => { BadRequest("Invalid Post Data")}
    }
  }

  def genFileName = {
    val date = new Date()
    new String(""+date.getTime())
  }


  def submitBlogUpdate = StackAction(AuthorityKey -> Contributor) { implicit request =>
    val item = Post.blogForm.bindFromRequest().get
    val content = item.content
   //
    database.withSession { implicit s =>
      val post = Post.getById(item.id).head
      val filename = post.content

      //val tags = js \ "tags"
      val newItem = new Post(item.id, item.title, item.postType, post.dateCreated, item.author, filename, Post.extraDataToJson(item.extraData),item.isDraft)
      repo.updateFile(filename,content, PostMeta.makeCommitMsg("Updated",newItem))
      Post.update(newItem)
      request.body.asFormUrlEncoded match {
        case None => {Logger.info("Couldn;t get data from request")}
        case Some(formData) => {
          formData.get("tags") match {
            case Some(tagData) => {
              tagData.foreach { tags:String =>
                tags.split(",").foreach { str:String =>
                  val tag = ContentTag.create(str.trim)
                  PostToTag.link(newItem.id, tag.id)
                }
              }
            }
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

  def updateBiography= DBAction  { implicit response =>
    val item = Biography.form.bindFromRequest().get

    database.withSession {  implicit s =>
      Biography.update(item)
    }
    Ok(""+item.id)
  }
}
