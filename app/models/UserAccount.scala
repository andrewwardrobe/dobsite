package models

import org.mindrot.jbcrypt.BCrypt
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation._
import play.api.db.slick._

import play.api.Play.current
import play.api.libs.json.{Json, JsValue}
import reactivemongo.bson.BSONObjectID
import scala.text._
import scala.util.matching.Regex

/**
 * Created by andrew on 23/12/14.
 */
case class UserAccount(_id: BSONObjectID, email: String, password: String, name: String, role: String, aliasLimit: Option[Int] = None) {


  def hasPermission(required: UserRole) = {
    UserRole.roleHasAuthority(userRole,required)
  }

  def userRole = {
    UserRole.valueOf(role)
  }

  def getAliasLimit = {
    aliasLimit match {
      case Some(x) => x
      case None => {
        userRole.aliasLimit
      }
    }
  }

}

