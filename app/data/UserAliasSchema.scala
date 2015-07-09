package data

import models.UserAlias

/**
 * Created by andrew on 09/07/15.
 */
trait UserAliasSchema {
  import play.api.db.slick.Config.driver.simple._


  class PostTable(tag:Tag) extends Table[UserAlias](tag,"posts"){



    def id = column[String]("ID",O.PrimaryKey)
    def userId = column[String]("USERID")
    def alias = column[String]("ALIAS")


    def * = (id,userId,alias) <> ((UserAlias.apply _).tupled, UserAlias.unapply _)
  }
}
