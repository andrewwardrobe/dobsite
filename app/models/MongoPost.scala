package models

import java.util.Date

import reactivemongo.bson.BSONObjectID

/**
 *
 * Created by andrew on 29/07/15.
 *
 */
case class MongoPost(_id: BSONObjectID, title:String, postType: Int, dateCreated :Date,author:String, content: String, extraData: String, isDraft: Boolean, userId:Option[BSONObjectID], tags: Option[Seq[String]])


