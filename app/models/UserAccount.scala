package models

import org.mindrot.jbcrypt.BCrypt
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation._
import play.api.db.slick._

import play.api.Play.current
import play.api.libs.json.{Json, JsValue}
import scala.text._
import scala.util.matching.Regex

/**
 * Created by andrew on 23/12/14.
 */
case class UserAccount(id: Int, email: String, password: String, name: String, role: UserRole){
  val json: JsValue = Json.obj(
    "id" -> id,
    "email" -> email,
    "name" -> name,
    "role" -> role.name
  )

  def hasPermission(required: UserRole) = {
    UserRole.roleHasAuthority(role,required)
  }
}

