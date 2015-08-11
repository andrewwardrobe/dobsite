package models

import play.api.data.{FormError, Form}
import play.api.data.Forms._
import play.api.data.format.Formatter
import reactivemongo.bson.BSONObjectID
import play.api.data.format.Formats._
import play.modules.reactivemongo.json.BSONFormats._
import scala.util.{Failure, Success}


/**
 *
 * Created by andrew on 02/08/15.
 *
 */


object Forms {


  def bsonMapper(id:String) = {
    val bson = BSONObjectID.parse(id)
    bson match {
      case Success(obj) => obj
      case Failure(ex) => BSONObjectID.generate
    }
  }
  val profileForm : Form[Profile] = Form {
    mapping (
      "id" -> text.transform[BSONObjectID]({ s => bsonMapper(s)},{b => b.stringify}),
      "userId" -> text.transform[BSONObjectID]({ s => bsonMapper(s)},{b => b.stringify}),
      "about" -> text,
      "avatar" -> text,
      "aliases" -> optional(seq(text))
    )(Profile.apply)(Profile.unapply _)
  }


  val blogForm: Form[MongoPost] = Form {
    mapping (
      "id" -> text.transform[BSONObjectID]({ s => bsonMapper(s)},{b => b.stringify}),
      "title" -> text,
      "postType" -> number,
      "dateCreated" -> date("yyyyMMddHHmmss"),
      "author" ->text,
      "content" -> text,
      "extraData" -> text,
      "isDraft" -> boolean,
      "userId" -> optional(text.transform[BSONObjectID]({ s => bsonMapper(s)},{b => b.stringify})),
      "tags" -> optional(seq(text))
    )(MongoPost.apply)(MongoPost.unapply _)
  }
}
