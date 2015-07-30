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
  implicit val format = Json.format[MongoPost]
}
