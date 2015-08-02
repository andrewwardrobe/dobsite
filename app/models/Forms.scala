package models

import play.api.data.Form
import play.api.data.Forms._
import reactivemongo.bson.BSONObjectID


/**
 *
 * Created by andrew on 02/08/15.
 *
 */
object Forms {
  val profileForm : Form[Profile] = Form {
    mapping (
      "id" -> ignored(BSONObjectID.generate), //Todo this will cause issues
      "userId" -> ignored(BSONObjectID.generate), //Todo so will this
      "about" -> text,
      "avatar" -> text,
      "aliases" -> optional(seq(text))
    )(Profile.apply)(Profile.unapply _)
  }
}
