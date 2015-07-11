package data

import models.ContentTag

/**
 * Created by andrew on 14/05/15.
 */
trait ContentTagFunctions { this: ContentTagSchema =>

  import play.api.db.slick.Config.driver.simple._

  import java.util.UUID


  def create(title:String)(implicit session: Session) = {
    val tag = new ContentTag(UUID.randomUUID.toString,title)
    tagsTable.insert(tag)
    tag
  }

  def get(id:String)(implicit session: Session) = {
    tagsTable.filter(_.id === id).list.head
  }

  def getAll (implicit session: Session) = {
    tagsTable.list
  }

  def deleteAll (implicit session: Session) = tagsTable.delete
}

