package models

import play.api.{Logger, Play}
import play.api.Play.current
import play.api.data.validation.Constraint
import play.api.data.{WrappedMapping, FormError, Mapping}

import scala.collection.mutable.ListBuffer

class UserRole(role: String){
  def name = role
  lazy val roles = loadRoles(role)

  lazy val authorities = loadAuthorities(name)

  def loadRoles(userRole:String):List[String] = {
    val builder = new ListBuffer[String]
    val roles = Play.application.configuration.getString(s"userrole.$userRole.roles").getOrElse("")
    builder ++= roles.split(",").map(_.trim).toList
    Play.application.configuration.getString(s"userrole.$userRole.auhtorities").getOrElse("").split(",").map(s => s.trim).toList.foreach{ rle =>
      if(rle!="")
        builder ++= loadRoles(rle)
    }
    builder.toList
  }

  def loadAuthorities(auth:String):List[String] ={
    val builder = new ListBuffer[String]
    builder += auth
    val roles = Play.application.configuration.getString(s"userrole.$auth.auhtorities").getOrElse("").split(",").map(s => s.trim).toList
    roles.foreach{ rle =>
      if(rle!="")
        builder ++= loadAuthorities(rle)
    }
    builder.toList
  }

  def hasAuthority(auth:String) = {
    authorities.contains(auth)
  }

  def hasPermission(perm:String) = {
    roles.contains(perm)
  }
}

object UserRole {



  case object Administrator extends UserRole("Administrator") {
    override def hasAuthority(auth:String) = true
    override def hasPermission(auth:String) = true
  }
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
    role.hasAuthority(authority.name)
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