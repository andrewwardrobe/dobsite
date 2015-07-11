package data

import play.api.db.DB

import scala.slick.jdbc.JdbcBackend._

/**
 *
 * Created by andrew on 11/07/15.
 *
 */
trait DataBase {

  import play.api.Play.current

  def database = Database.forDataSource(DB.getDataSource())
}
