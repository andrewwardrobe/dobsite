package models

import play.api.libs.json.Json

/**
 *
 * Created by andrew on 30/07/15.
 *
 */
object JsonFormats {
  import play.modules.reactivemongo.json.ImplicitBSONHandlers
  import play.modules.reactivemongo.json.BSONFormats._
  implicit val postFormat = Json.format[Post]
  implicit val discFormat = Json.format[Discography]
  implicit val bioFormat = Json.format[Biography]
  implicit val userFormat = Json.format[UserAccount]
  implicit val profileFormat = Json.format[Profile]
}
