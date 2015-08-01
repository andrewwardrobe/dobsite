package models

import reactivemongo.bson.BSONObjectID

/**
 *
 * Created by andrew on 01/08/15.
 *
 */
case class Profile(_id:BSONObjectID,userId: BSONObjectID, about:String, avatar:String, aliases : Option[Seq[String]])
