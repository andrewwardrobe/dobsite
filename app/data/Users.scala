package data

import models.{UserRole, UserAccount}
import org.mindrot.jbcrypt.BCrypt
import play.api.db.DB
import play.api.libs.json.Json
import reactivemongo.bson.BSONDocument
import reactivemongo.core.commands.Count

import scala.concurrent.{Future, Await}

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.duration.DurationInt
import scala.util.Success

/**
 * Created by andrew on 14/05/15.
 */
object Users extends DAOBase[UserAccount]("users") {

  import models.JsonFormats._

  def findByEmail(email: String) = {
    find(Json.obj("email" -> email))
  }

  def findByName(name: String) = {
    find(Json.obj("name" -> name))
  }

  def authenticate(email: String, password: String): Option[UserAccount] = {
    email.contains("@") match {
      case true =>
        val result = findByEmail(email)
        val accounts = Await.result(result,10 seconds)
        accounts.find(acc => BCrypt.checkpw(password,acc.password))

      case false =>
        val result = findByName(email)
        val accounts = Await.result(result,10 seconds)
        accounts.find(acc => BCrypt.checkpw(password,acc.password))
    }
  }



  def changeRole(user: UserAccount, roleType :String) = {
    update(user._id.stringify,user.copy(role = roleType))
  }

  def create(user: UserAccount) = {
    val encPass = BCrypt.hashpw(user.password, BCrypt.gensalt())
    collection.count().andThen{ cnt =>
      cnt match {
        case Success(0) => {
          insert(user.copy(password = encPass, role = "Administrator")).map { res =>
            user
          }
        }
        case Success(_) => insert(user.copy(password = encPass)).map { res =>
          user
        }
      }
    }
  }

  def create(user: UserAccount, requiredRole: String) = {
    val encPass = BCrypt.hashpw(user.password, BCrypt.gensalt())
    insert(user.copy(role = requiredRole, password = encPass))
  }

  def newPasswd(userAccount: UserAccount, passwd: String) = {
    val encPass = BCrypt.hashpw(passwd, BCrypt.gensalt())
    update(userAccount._id.stringify, userAccount.copy(password = passwd))
  }

  def newEmail(userAccount: UserAccount, newEmail: String) = {
    update(userAccount._id.stringify, userAccount.copy(email = newEmail))
  }

  def getUserNameCount(name:String) = {
    val nameRegex = "^" + name + "$"
    count(Json.obj("name" -> Json.obj("$regex" -> nameRegex , "$options" -> "-i") ))
  }

  def getEmailCount(email:String) = {
    count(Json.obj("email" -> email ))
  }

  def getUsersLike(name :String) ={
    find(Json.obj( "name" -> Json.obj("$regex" -> s"^${name}.*")))
  }

  def aliasAvailable(alias:String) = {
    getUserNameCount(alias).flatMap { count =>
      if (count > 0 ){
        Future(false)
      }else{
        Profiles.aliasAvailable(alias)
      }
    }
  }
}



