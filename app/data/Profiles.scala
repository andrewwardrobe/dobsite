package data

import models.UserProfile
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json


/**
 * Created by andrew on 19/05/15.
 */
object Profiles extends UserProfileSchema with UserProfileAccess{

  def toJson(profile:UserProfile) = {
    implicit val profileFormat = Json.format[UserProfile]
    Json.toJson(profile)
  }

  val form : Form[UserProfile] = Form {
    mapping (
      "id" -> number,
      "userId" -> number,
      "about" -> text,
      "avatar" -> text
    )(UserProfile.apply)(UserProfile.unapply _)
  }
}
