package data

import java.util.Date

import org.joda.time.DateTime
import play.api.libs.json.Json
import reactivemongo.bson.{BSONDateTime, BSONDocument}

/**
 *
 * Created by andrew on 04/08/15.
 *
 */
object ContentQueries {
  def liveContentByAuthorBeforeDate(author:String, typ : Int, date :Date) = Json.obj(
    "postType" -> typ,
    "author" -> author,
    "isDraft" -> false,
    "dateCreated" -> Json.obj("$lt" ->  date)
  )

  def liveContentByAuthorLatestFirst(author:String) = Json.obj(
    "$query" ->Json.obj(
      "author" -> author,
      "isDraft" -> false
    ),
    "$orderby" -> Json.obj("dateCreated" -> -1)
  )

  def liveContentByTypeLatestFirst(typ: Int, date :Date) = Json.obj(
    "$query" ->Json.obj(
      "postType" -> typ,
      "isDraft" -> false,
      "dateCreated" -> Json.obj("$lt" ->  date)
    ),"$orderby" -> Json.obj("dateCreated" -> -1)
  )

  def liveContentByTypeLatestFirst(typ: Int) = Json.obj(
    "$query" ->Json.obj(
      "postType" -> typ,
      "isDraft" -> false
    ),"$orderby" -> Json.obj("dateCreated" -> -1)
  )

  def liveContentByAuthorBeforeDateLatestFirst(author:String, typ : Int, date :Date) = Json.obj(
    "$query" ->Json.obj(
      "postType" -> typ,
      "author" -> author,
      "isDraft" -> false,
      "dateCreated" -> Json.obj("$lt" ->  date)
    ),"$orderby" -> Json.obj("dateCreated" -> -1)
  )

  def liveContentByUserLatestFirst(user:String) = Json.obj(
    "$query" ->Json.obj(
      "userId" -> Json.obj("$oid" -> user),
      "isDraft" -> false
    ),"$orderby" -> Json.obj("dateCreated" -> -1)
  )

  def draftContentByUserLatestFirst(user:String) = Json.obj(
    "$query" ->Json.obj(
      "userId" -> Json.obj("$oid" -> user),
      "isDraft" -> true
    ),"$orderby" -> Json.obj("dateCreated" -> -1)
  )


  def byType(typ :Int) = Json.obj(
    "postType" -> typ
  )

  def dateReverse() = Json.obj(
    "$query" -> Json.obj(),
    "isDraft" -> false,
    "$orderby" -> Json.obj("dateCreated" -> -1)
  )

  def dateReverse(date:Date) = Json.obj(
    "$query" -> Json.obj( "dateCreated" -> Json.obj("$lt" -> date),
    "isDraft" -> false ),
    "$orderby" -> Json.obj("dateCreated" -> -1)
  )

  def dateReverse(typeId:Int,date:Date) = Json.obj(
    "$query" -> Json.obj(
      "dateCreated" -> Json.obj("$lt" ->  date),
      "isDraft" -> false,
      "postType" -> typeId),
    "$orderby" -> Json.obj("dateCreated" -> -1)
  )

  def dateReverseWithDrafts() = Json.obj(
    "$query" -> Json.obj(),
    "$orderby" -> Json.obj("dateCreated" -> -1)
  )

  def dateReverseWithDrafts(date:Date) = Json.obj(
    "$query" -> Json.obj( "dateCreated" -> Json.obj("$lt" -> date)),
    "$orderby" -> Json.obj("dateCreated" -> -1)
  )

  def dateReverseWithDrafts(typeId:Int,date:Date) = Json.obj(
    "$query" -> Json.obj(
      "dateCreated" -> Json.obj("$lt" ->  date),
      "postType" -> typeId),
    "$orderby" -> Json.obj("dateCreated" -> -1)
  )

  def dateReverseJustDrafts(date:Date) = Json.obj(
    "$query" -> Json.obj( "dateCreated" -> Json.obj("$lt" -> date),
      "isDraft" -> true ),
    "$orderby" -> Json.obj("dateCreated" -> -1)
  )

  def dateJustDrafts(typeId:Int,date:Date) = Json.obj(
    "$query" -> Json.obj(
      "dateCreated" -> Json.obj("$lt" ->  date),
      "isDraft" -> true,
      "postType" -> typeId),
    "$orderby" -> Json.obj("dateCreated" -> -1)
  )

  def dateReverseJustDrafts() = Json.obj(
    "$query" -> Json.obj(),
    "isDraft" -> true,
    "$orderby" -> Json.obj("dateCreated" -> -1)
  )

  def dateTest(date:Date) = {
    val dt = new DateTime(date)
    BSONDocument(
        "dateCreated" -> BSONDocument("$lte" -> BSONDateTime(dt.getMillis))
  )}

  def all = Json.obj()
}
