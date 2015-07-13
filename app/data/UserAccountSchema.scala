package data

import models.{UserAccount, UserRole}


trait UserAccountSchema {
  import play.api.db.slick.Config.driver.simple._
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

    def aliasLimit = column[Option[Int]]("ALIAS_LIMIT", O.Nullable)

    def * = (id, email, password, name, role, aliasLimit) <>((UserAccount.apply _).tupled, UserAccount.unapply)
  }

  val accounts = TableQuery[UserAccountTable]
}
