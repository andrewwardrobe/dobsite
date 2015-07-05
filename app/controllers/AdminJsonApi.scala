/**
 * Created by andrew on 25/01/15.
 */
package controllers

import java.util.{Date, UUID}

import com.daoostinboyeez.git.GitRepo
import controllers.Application._
import data.{Content, UserAccounts}
import jp.t2v.lab.play2.auth._
import jp.t2v.lab.play2.auth.AuthElement
import models._

import models.UserRole.{Administrator, Contributor, NormalUser}
import play.api.{Play, Logger}
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import play.api.db.DB
import play.api.db.slick.DBAction

import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import scala.slick.jdbc.JdbcBackend._
import scala.text

import play.api.libs.json._
import play.api.libs.json.Json._


object AdminJsonApi extends Controller with AuthElement with StandardAuthConfig {
  implicit val discFormat = Json.format[Discography]
  implicit val bioFormat = Json.format[Biography]

  private val defaultAuthor = Play.application.configuration.getString("content.defaultauthor").getOrElse("Da Oostin Boyeez")

  def getUsers(name:String) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    database.withSession{ implicit s =>
      Ok(toJson(UserAccounts.getUsersLike(name)))
    }
  }


  def getUser(name:String) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    database.withSession{ implicit s =>
      Ok(toJson(UserAccounts.findByName(name).head.json))
    }
  }

  def processBiographies(bios: Seq[Biography]) = {
    bios.foreach { bio: Biography =>
      val extraData = s"thumb=${bio.thumbnail}"
      val post = new ContentPost(UUID.randomUUID().toString(), bio.name, ContentTypeMap("Discography"), new Date(), defaultAuthor, bio.text, extraData, false, None)
      Content.save(post, GitRepo.apply(), None, None)
    }
    bios.length
  }

  def processDiscographies(discs: Seq[Discography]) = {
    discs.foreach { disc: Discography =>
      val str = new StringBuilder()
      str.append("<div id=\"content\"><ol>")
      disc.tracks.foreach { track =>
        str.append(s"<li>${track}</li>")
      }
      str.append("</ol></div>")
      val content = str.toString
      val extraData = s"thumb=${disc.artwork}\ndiscType=${disc.discType}"
      val post = new ContentPost(UUID.randomUUID().toString(), disc.title, ContentTypeMap("Discography"), new Date(), defaultAuthor, content, extraData, false, None)
      Content.save(post, GitRepo.apply(), None, None)
    }
    discs.length
  }

  def insertDiscographies() = StackAction(AuthorityKey -> Administrator) { implicit request =>
    request.body.asJson match {
      case Some(json) => {

        val discs = (json \ "discographies").validate[List[Discography]] match {
          case p: JsSuccess[List[Discography]] => {processDiscographies(p.get)}
          case err: JsError => {}
        }

        val bios = (json \ "biographies").validate[List[Biography]] match {
          case p: JsSuccess[List[Biography]] => {processBiographies(p.get)}
          case err: JsError => {}
        }
        Ok(s"Inserted ${discs} discographies, ${bios} biography")
      }
      case None => {     BadRequest("No Request Body request body")      }
    }
  }
}
