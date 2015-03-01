/**
 * Created by andrew on 25/01/15.
 */
package controllers

import controllers.Application._
import jp.t2v.lab.play2.auth._
import jp.t2v.lab.play2.auth.AuthElement
import models.{UserAccount, Post$}

import models.UserRole.{Administrator, Contributor, NormalUser}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import play.api.db.DB
import play.api.db.slick.DBAction

import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import scala.slick.jdbc.JdbcBackend._
import scala.text

import play.api.libs.json.{JsValue, Json}
import play.api.libs.json.Json._

object AdminJsonApi extends Controller with AuthElement with AuthConfigImpl {

  def getUsers(name:String) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    database.withSession{ implicit s =>
      Ok(toJson(UserAccount.getUsersLike(name)))
    }
  }


  def getUser(name:String) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    database.withSession{ implicit s =>
      Ok(toJson(UserAccount.findByName(name).head.json))
    }
  }
}
