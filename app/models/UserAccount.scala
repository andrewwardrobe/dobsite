package models

import org.mindrot.jbcrypt.BCrypt
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.Play.current
import scala.text._

/**
 * Created by andrew on 23/12/14.
 */
case class UserAccount(id: Int, email: String, password: String, name: String, role: UserRole)

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

  def authenticate(email: String, password: String)(implicit s: Session): Option[UserAccount] = {
    findByEmail(email).filter { account => BCrypt.checkpw(password, account.password) }
    //Some(new UserAccount(1,"Test User","None","MC Donalds",UserRole.valueOf("NormalUser")))
  }

  def create(userAccount: UserAccount)(implicit s: Session) =  {
    import userAccount._
    val encPass = BCrypt.hashpw(password,BCrypt.gensalt())
    val insertAcc = new UserAccount(id,email,encPass,name,UserRole.valueOf("NormalUser"))
    accounts.insert(insertAcc)
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
}