package models

import org.mindrot.jbcrypt.BCrypt
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.Play.current
import play.api.libs.json.{Json, JsValue}
import scala.text._
import scala.util.matching.Regex

/**
 * Created by andrew on 23/12/14.
 */
case class UserAccount(id: Int, email: String, password: String, name: String, role: UserRole){
  val json: JsValue = Json.obj(
    "id" -> id,
    "email" -> email,
    "name" -> name,
    "role" -> role.name
  )
}

object UserAccount {



  class UserAccountTable(tag: Tag) extends Table[UserAccount](tag, "users") {

    implicit val roleMapper =
      MappedColumnType.base[UserRole, String](
        s => s.name,
        s => UserRole.valueOf(s)
    )

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def email = column[String]("EMAIL",O.NotNull)
    def password = column[String]("PASSWORD")
    def name = column[String]("NAME")
    def role = column[UserRole]("ROLE")

    def * = (id, email, password, name, role) <> ((UserAccount.apply _).tupled, UserAccount.unapply)
  }

  val accounts  = TableQuery[UserAccountTable]

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
      case _ => new UserAccount(id, email, encPass, name, UserRole.valueOf("NormalUser"))
    }
    accounts.insert(insertAcc)
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
    accounts.insert(insertAcc)
  }

  def getUserName(email: String)(implicit s: Session) = {
    val acc = accounts.filter(_.email === email).list
    acc match {
      case Nil => None
      case _ => Some(acc.head.name)
    }
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