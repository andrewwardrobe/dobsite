package data

import models.UserAccount
import play.api.db.DB

import scala.slick.jdbc.JdbcBackend._

/**
 * Created by andrew on 14/05/15.
 */
object UserAccounts extends UserAccountFunctions with UserAccountSchema with UserAliasFunctions with UserAliasSchema with DataBase {

}
