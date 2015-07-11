package data

import models.{UserAccount, UserRole}
import org.mindrot.jbcrypt.BCrypt
import play.api.db.slick._

/**
 * Created by andrew on 14/05/15.
 */
trait UserAccountFunctions {this: UserAccountSchema =>

  import play.api.db.slick.Config.driver.simple._



  val accounts  = TableQuery[UserAccountTable]

  def getAliases(user:UserAccount)(implicit s: Session) = {
    (for {
      ua <- UserAliasDAO.userAlias if ua.userId === user.id

    } yield ua).list map { _.alias }
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
    //Some(new UserAccount(1,"Test User","None","MC Donalds",UserRole.valueOf("NormalUser")))
  }

  def create(userAccount: UserAccount)(implicit s: Session) =  {
    import userAccount._
    val encPass = BCrypt.hashpw(password,BCrypt.gensalt())
    val insertAcc = accounts.length.run match {
      case 0 => new UserAccount(id, email, encPass, name, UserRole.valueOf("Administrator"))
      case _ => new UserAccount(id, email, encPass, name, role)
    }
    accounts returning accounts.map(_.id) += insertAcc
  }

  def update(userAccount: UserAccount)(implicit s: Session) = {
    accounts.update(userAccount)
  }

  def newPasswd(userAccount: UserAccount, passwd: String) (implicit s: Session)= {
    val encPass = BCrypt.hashpw(passwd,BCrypt.gensalt())
    import userAccount._
    val updateAcc = new UserAccount(id, email, encPass, name, role)
    accounts.insertOrUpdate(updateAcc)
  }

  def newEmail(userAccount: UserAccount,newEmail: String) (implicit s: Session)= {

    import userAccount._
    val updateAcc = new UserAccount(id, newEmail, password, name, role)
    accounts.insertOrUpdate(updateAcc)
  }

  def changeRole(userAccount: UserAccount, roleType: String)(implicit s: Session)  = {
    import userAccount._
    val newRole = UserRole.valueOf(roleType)
    newRole match {
      case _:UserRole =>
        val updateAcc = new UserAccount(id, email, password, name, newRole)
        accounts.insertOrUpdate(updateAcc)
        0

    }
  }

  def create(userAccount: UserAccount, userRole:String)(implicit s: Session) =  {
    import userAccount._
    val encPass = BCrypt.hashpw(password,BCrypt.gensalt())
    val insertAcc = new UserAccount(id,email,encPass,name,UserRole.valueOf(userRole))
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