package data

import models.{UserAccount, UserRole}
import org.mindrot.jbcrypt.BCrypt
import play.api.db.slick.Session
import play.api.db.slick._

import scala.slick.jdbc.JdbcBackend._

/**
 * Created by andrew on 14/05/15.
 */
trait UserAccountFunctions {
  this: UserAccountSchema with DataBase =>

  import play.api.db.slick.Config.driver.simple._


  def getAliasesAsString(user: UserAccount)(implicit s: Session) = {
    (for {
      ua <- UserAliasDAO.userAlias if ua.userId === user._id

    } yield ua).list map { _.alias }
  }

  def getAliases(user: UserAccount)(implicit s: Session) = {
    (for {
      ua <- UserAliasDAO.userAlias if ua.userId === user._id

    } yield ua).list
  }

  def getUserAliases(user: UserAccount) = {
    database.withSession { implicit session =>
      getAliases(user)
    }
  }

  def getUserAliasesAsString(user: UserAccount) = {
    database.withSession { implicit session =>
      getAliasesAsString(user)
    }
  }

  def getProfile(user:UserAccount)(implicit s: Session) = {
    (for {
      p <- Profiles.profiles
    } yield p).list.headOption
  }

  def findById(id :Int)(implicit s: Session): Option[UserAccount] = {
    val acc = accounts.filter(_.id === id).list
    acc match {
      case Nil => None
      case _ => Some(acc.head)
    }

  }

  def findByEmail(email :String)(implicit s: Session): Option[UserAccount] = {
    val acc = accounts.filter(_.email === email).list
    acc match {
      case Nil => None
      case _ => Some(acc.head)
    }

  }

  def findByName(name :String)(implicit s: Session): Option[UserAccount] = {
    val acc = accounts.filter(_.name.toLowerCase === name.toLowerCase).list
    acc match {
      case Nil => None
      case _ => Some(acc.head)
    }

  }

  def authenticate(email: String, password: String)(implicit s: Session): Option[UserAccount] = {
    email.contains("@") match {
      case true =>
        findByEmail(email).filter { account => BCrypt.checkpw(password, account.password)}
      case false =>
        findByName(email).filter { account => BCrypt.checkpw(password, account.password)}
    }
  }

  def create(userAccount: UserAccount)(implicit s: Session) =  {
    import userAccount._
    val encPass = BCrypt.hashpw(password,BCrypt.gensalt())
    val insertAcc = accounts.length.run match {
      case 0 => new UserAccount(_id, email, encPass, name, "Administrator")
      case _ => new UserAccount(_id, email, encPass, name, role)
    }
    accounts returning accounts.map(_.id) += insertAcc
  }

  def update(userAccount: UserAccount)(implicit s: Session) = {
    accounts.update(userAccount)
  }

  def newPasswd(userAccount: UserAccount, passwd: String) (implicit s: Session)= {
    val encPass = BCrypt.hashpw(passwd,BCrypt.gensalt())
    import userAccount._
    val updateAcc = new UserAccount(_id, email, encPass, name, role)
    accounts.insertOrUpdate(updateAcc)
  }

  def newEmail(userAccount: UserAccount,newEmail: String) (implicit s: Session)= {

    import userAccount._
    val updateAcc = new UserAccount(_id, newEmail, password, name, role)
    accounts.insertOrUpdate(updateAcc)
  }

  def changeRole(userAccount: UserAccount, roleType: String)(implicit s: Session)  = {
    import userAccount._
    val newRole = UserRole.valueOf(roleType)
    newRole match {
      case _:UserRole =>
        val updateAcc = new UserAccount(_id, email, password, name, roleType)
        accounts.insertOrUpdate(updateAcc)
        0

    }
  }

  def create(userAccount: UserAccount, requiredRole:String)(implicit s: Session) =  {
    import userAccount._
    val encPass = BCrypt.hashpw(password,BCrypt.gensalt())
    val insertAcc = new UserAccount(_id, email, encPass, name, requiredRole)
    accounts returning accounts.map(_.id) += insertAcc
  }

  def getUserName(email: String)(implicit s: Session) = {
    val acc = accounts.filter(_.email === email).list
    acc match {
      case Nil => None
      case _ => Some(acc.head.name)
    }
  }

  def deleteAll(implicit s: Session) = {
    accounts.delete
  }

  def getUserNameCount(name:String)(implicit s: Session) =  {
    accounts.filter(_.name.toLowerCase === name.toLowerCase).list.length
  }

  def getEmailCount(email:String)(implicit s: Session) =  {
    accounts.filter(_.email.toLowerCase === email.toLowerCase).list.length
  }

  def getUsersLike(name:String)(implicit s: Session) = {
    val query = for {
      account <- accounts if account.name.toLowerCase.startsWith(name.toLowerCase)
    } yield (account.name)
    query.list
  }


}