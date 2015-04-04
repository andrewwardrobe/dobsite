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
  case object TrustedContributor extends UserRole("TrustedContributor")
  case object InActiveUser extends UserRole("InActiveUser")
  def valueOf(value: String): UserRole = value match {
    case "Administrator" => Administrator
    case "NormalUser" => NormalUser
    case "TrustedContributor" => TrustedContributor
    case "Contributor" => Contributor
    case "InActiveUser" => InActiveUser
    case _ => throw new IllegalArgumentException()
  }

  def roleHasAuthority(role: UserRole, authority: UserRole) = {
    (role, authority) match {
      case (Administrator, _)       => true
      case(TrustedContributor,TrustedContributor) => true
      case(TrustedContributor,Contributor) => true
      case(TrustedContributor,NormalUser) => true
      case(Contributor,Contributor) => true
      case(Contributor,NormalUser) => true
      case(NormalUser, NormalUser) => true
      case(InActiveUser, InActiveUser) => true
      case _                        => false
    }
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