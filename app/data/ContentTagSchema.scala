package data

import models.ContentTag

/**
 * Created by andrew on 14/05/15.
 */

trait ContentTagSchema {

  import play.api.db.slick.Config.driver.simple._

  class TagTable(tag: Tag) extends Table[ContentTag](tag, "POSTTAGS") {
    //Pri Key
    def id = column[String]("id", O.PrimaryKey)

    def title = column[String]("title")

    def * = (id, title) <>((ContentTag.apply _).tupled, ContentTag.unapply)
  }

}

