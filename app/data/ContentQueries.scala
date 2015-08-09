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
    "postType" -> typ,
    "author" -> author,
    "dateCreated" -> Json.obj("$lt" -> Json.obj("$date" -> date))
  )
  def byType(typ :Int) = Json.obj(
    "postType" -> typ
  )

  def dateReverse = Json.obj(
    "$query" -> Json.obj(),
    "$orderby" -> Json.obj("dateCreated" -> -1)
  )

  def dateReverse(date:Date) = Json.obj(
    "$query" -> Json.obj( "dateCreated" -> Json.obj("$lte" -> Json.obj("$date" -> date))),
    "$orderby" -> Json.obj("dateCreated" -> -1)
  )

  def dateReverse(typeId:Int,date:Date) = Json.obj(
    "$query" -> Json.obj(
      "dateCreated" -> Json.obj("$lt" -> Json.obj("$date" -> date)),
      "postType" -> typeId),
    "$orderby" -> Json.obj("dateCreated" -> -1)
  )


  def all = Json.obj()
}
