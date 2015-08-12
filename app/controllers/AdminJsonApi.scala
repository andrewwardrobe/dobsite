/**
 * Created by andrew on 25/01/15.
 */
package controllers

import java.util.{Date, UUID}

import com.daoostinboyeez.git.GitRepo
import controllers.Application._
import data.{Content, Users}
import jp.t2v.lab.play2.auth._
import jp.t2v.lab.play2.auth.AuthElement
import models._

import models.UserRole.{Administrator, Contributor, NormalUser}
import play.api.{Play, Logger}
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import reactivemongo.bson.BSONObjectID
import scala.text
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object AdminJsonApi extends Controller with AuthElement with StandardAuthConfig {

  import models.JsonFormats._

  private val defaultAuthor = Play.application.configuration.getString("content.defaultauthor").getOrElse("Da Oostin Boyeez")

  def getUsers(name:String) = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    Users.getUsersLike(name).map { users =>
      Ok(toJson(users))
    }

  }


  def getUser(name:String) = AsyncStack(AuthorityKey -> Administrator) { implicit request =>
    Users.findByName(name).map { users =>
      users.headOption match {
        case Some(user) => Ok(toJson(user))
        case None => NotFound("User Not Found")
      }
    }
  }

  def processBiographies(bios: Seq[Biography]) = {
    bios.foreach { bio: Biography =>
      val ed = new scala.collection.mutable.HashMap[String,String]()
      ed.put("thumb",bio.thumbnail)
      ed.put("crew",bio.crew)
      val exData =  new scala.collection.immutable.HashMap[String,String]() ++ ed
      val extraData = s"thumb=${bio.thumbnail}\n,crew=${bio.crew}"

      val post = new Post(BSONObjectID.generate, bio.name, ContentTypeMap("Discography"), new Date(), defaultAuthor, bio.text, Some(exData), false, None, None)
      Content.create(post, GitRepo.apply())
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
      val ed = new scala.collection.mutable.HashMap[String,String]()
      ed.put("thumb",disc.artwork)
      ed.put("discType",disc.discType)
      val exData =  new scala.collection.immutable.HashMap[String,String]() ++ ed
      val post = new Post(BSONObjectID.generate, disc.title, ContentTypeMap("Discography"), new Date(), defaultAuthor, content, Some(exData), false, None, None)
      Content.create(post, GitRepo.apply())
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
