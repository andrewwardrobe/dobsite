package models

import reactivemongo.bson.BSONObjectID

/**
 *
 * Created by andrew on 29/07/15.
 *
 */
case class MongoPost(_id: BSONObjectID, title:String, postType: Int,author:String, content: String, extraData: String, isDraft: Boolean, userId:Option[Int])

