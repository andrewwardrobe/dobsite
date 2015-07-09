package data


import models.UserAlias

/**
 * Created by andrew on 09/07/15.
 */
trait UserAliasSchema {
  import play.api.db.slick.Config.driver.simple._


  class UserAliasTable(tag:Tag) extends Table[UserAlias](tag,"user_alias"){



    def id = column[String]("ID",O.PrimaryKey)
    def userId = column[Int]("USERID")
    def alias = column[String]("ALIAS")

    def userFK = foreignKey("ACCOUNT_FK", userId,UserAccounts.accounts)(account => account.id)
    def * = (id,userId,alias) <> ((UserAlias.apply _).tupled, UserAlias.unapply _)
  }

  val userAlias  = TableQuery[UserAliasTable]

}
