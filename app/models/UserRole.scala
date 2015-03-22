package models

import play.api.data.validation.Constraint
import play.api.data.{WrappedMapping, FormError, Mapping}

class UserRole(role: String){
  def name = role
}

object UserRole {
  case object Administrator extends UserRole("Administrator")
  case object NormalUser extends UserRole("NormalUser")
  case object Contributor extends UserRole("Contributor")
  case object InActiveUser extends UserRole("InActiveUser")
  def valueOf(value: String): UserRole = value match {
    case "Administrator" => Administrator
    case "NormalUser" => NormalUser
    case "Contributor" => Contributor
    case "InActiveUser" => InActiveUser
    case _ => throw new IllegalArgumentException()
  }
}

class UserRoleMapping(val key: String = "") extends Mapping[UserRole]{
  val constraints = Nil

  override val mappings = Seq(this)

  override def verifying(constraints: Constraint[UserRole]*): Mapping[UserRole] = WrappedMapping[UserRole,UserRole] (this, x=>x, x=>x,constraints)

  override def unbind(value: UserRole): Map[String, String] = {
    Map(key -> value.name)
  }

  override def withPrefix(prefix: String): Mapping[UserRole] = new UserRoleMapping(prefix+key)

  override def unbindAndValidate(value: UserRole): (Map[String, String], Seq[FormError]) = {

    (Map(key -> value.name),Seq(FormError(key,"Invalid")))
  }

  override def bind(data: Map[String, String]): Either[Seq[FormError], UserRole] = {
    val userType = data.get("role")
    userType match {
      case Some(x) => Right(UserRole.valueOf(userType.get))
      case None => Left(Seq(FormError(key,"Not Found")))
    }

  }
}