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
    val query = Json.obj("userId" -> Json.obj("$oid" -> id.stringify))
    find(query)
  }

  def addAlias(user: UserAccount,alias :String) = {
    getByUserId(user._id).map{ users =>
      users.headOption match {
        case Some(profile) => {
          val aliasList = profile.aliases.getOrElse(Nil) ++: List(alias)
          val updated = profile.copy(aliases = Some(aliasList))
          update(profile._id.stringify,updated).map{ res =>
            res.ok match {
              case true =>
                updated
              case false => null
            }
          }

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

  def getAliasesAsString(user: UserAccount) = {
    getByUserId(user._id).map{ users =>
      users.head.aliases.getOrElse(Vector())
    }
  }


}
