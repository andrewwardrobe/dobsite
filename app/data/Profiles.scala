package data

import com.daoostinboyeez.site.exceptions.AliasLimitReachedException
import models.{UserRole, UserAccount, Profile}
import play.api.Logger
import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

/**
 *
 * Created by andrew on 01/08/15.
 *
 */
object Profiles extends DAOBase[Profile]("profiles"){
  import models.JsonFormats._

  def findByUserId(id: String) = {
    find(Json.obj("userId" -> Json.obj("$oid" -> id)))
  }

  def findByUserId(id: BSONObjectID) = {
    val query = Json.obj("userId" -> Json.obj("$oid" -> id.stringify))
    find(query)
  }

  def addAlias(profile:Profile,alias :String) =  {
    val aliasList = profile.aliases.getOrElse(Nil) ++: List(alias)
    val updated = profile.copy(aliases = Some(aliasList))

    update(profile._id.stringify,updated)
  }

  def addAlias(user:UserAccount,alias :String) =  {
    findByUserId(user._id).map { profiles =>
      val profile = profiles.head
      val aliasList = profile.aliases.getOrElse(Nil) ++: List(alias)
      val updated = profile.copy(aliases = Some(aliasList))

      update(profile._id.stringify, updated)
    }
  }

  def aliasAvailable(alias: String) = {
    val aliasRegex = "^" + alias + "$"
    count(Json.obj("aliases" -> Json.obj("$regex" -> aliasRegex , "$options" -> "-i" ))).map { result =>
      result match {
        case 0 => true
        case _ => false
      }
    }
  }

  def getAliasesAsString(user: UserAccount) = {
    findByUserId(user._id).map{ users =>
      users.head.aliases.getOrElse(Vector())
    }
  }


}
