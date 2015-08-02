package data

import models.{UserAccount, Profile}
import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import play.api.libs.concurrent.Execution.Implicits.defaultContext
/**
 *
 * Created by andrew on 01/08/15.
 *
 */
object UserProfiles extends DAOBase[Profile]("profiles"){
  import models.JsonFormats._

  def getByUserId(id: String) = {
    find(Json.obj("userId" -> Json.obj("$oid" -> id)))
  }

  def getByUserId(id: BSONObjectID) = {
    find(Json.obj("userId" -> Json.obj("$oid" -> id.toString())))
  }

  def addAlias(user: UserAccount,alias :String) = {
    getByUserId(user._id).map{ users =>
      users.headOption match {
        case Some(profile) => {
          val aliasList = profile.aliases.getOrElse(Nil) ++: List(alias)
          update(profile._id.toString(),profile.copy(aliases = Some(aliasList)))
        }
      }
    }
  }

  def aliasAvailable(alias: String) = {
    count(Json.obj("aliases" -> alias )).map { result =>
      result match {
        case 0 => true
        case _ => false
      }
    }
  }
}
