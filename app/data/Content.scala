package data

import models.MongoPost
import play.api.libs.json.Json


/**
 * Created by andrew on 14/05/15.
 */
object Content extends DAOBase[MongoPost]("posts"){

  import models.JsonFormats._
  def findByType(typeId: Int) = {
    find(Json.obj("typeId" -> typeId ))
  }

  //Todo write this
  def save = {

  }

}
