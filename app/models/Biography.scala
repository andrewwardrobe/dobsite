package models

import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._

/**
 * Created by andrew on 14/09/14.
 */
case class Biography(id: Int, name: String, bioType: Int,
                     imagePath: String, thumbPath: String, bioText: String)

object Biography{
  class BiographyTable(tag:Tag) extends Table[Biography](tag,"biography"){
    def id = column[Int]("ID",O.PrimaryKey,O.AutoInc)
    def name = column[String]("NAME")
    def bioType = column[Int]("BIO_TYPE")
    def imagePath = column[String]("IMAGE_PATH")
    def thumbPath = column[String]("THUMB_PATH")
    def bioText = column[String]("BIO_TEXT")

    def * = (id,name,bioType,imagePath,thumbPath,bioText) <> ((Biography.apply _).tupled,Biography.unapply)

  }

  val biography = TableQuery[BiographyTable]

  def getAll(implicit s: Session) = {biography.list}

  def getByType(bioType: Int)(implicit s: Session) = { biography.filter(_.bioType === bioType).list }

  def getByName(name: String)(implicit s: Session) = { biography.filter(_.name === name).list }

  def getById(id: Int)(implicit s: Session) = { biography.filter(_.id === id).list }

  def insert(bio: Biography)(implicit s: Session) = {biography.insert(bio)}

  def insert(bio: Seq[Biography])(implicit s: Session) = { bio.foreach{biography.insert(_)} }

  def delete(id: Int)(implicit s:Session) = {biography.filter(_.id === id).delete }

  def deleteByName(name: String)(implicit s:Session) = {biography.filter(_.name === name).delete }


}

