package data

import java.util.Date

import play.api.libs.json.Json

/**
 *
 * Created by andrew on 04/08/15.
 *
 */
object ContentQueries {
  def liveContentByAuthorBeforeDate(author:String, typ : Int, date :Date) = Json.obj(
    "typeId" -> typ,
    "author" -> author,
    "dateCreated" -> Json.obj("$lt" -> Json.obj("$date" -> date))
  )
  def byType(typ :Int) = Json.obj(
    "typeId" -> typ
  )
}
