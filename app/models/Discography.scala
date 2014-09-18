package models

/**
 * Created by andrew on 04/09/14.
 */

import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._



case class Discography(id: Int , name: String, releaseType: Int, imagePath: String)

object Discography {
  class DiscographyTable(tag:Tag) extends Table[Discography](tag,"discography"){
    def id = column[Int]("ID", O.PrimaryKey,O.AutoInc)
    def name = column[String]("NAME")
    def releaseType = column[Int]("RELEASE_TYPE")
    def imagePath = column[String]("IMAGE")
    def * = (id, name, releaseType, imagePath) <> ((Discography.apply _).tupled, Discography.unapply)
  }

  val discography = TableQuery[DiscographyTable]

  def get(implicit s: Session) = { discography.list }

  def insert(disco :Discography)(implicit s: Session) = { discography.insert(disco) }
  def insert(disco :Seq[Discography])(implicit s: Session) = { disco.foreach{ discography.insert(_) } }

  def getByName(name: String)(implicit s: Session)  = {
    val results = discography.filter(_.name === name)
    results.list
  }

  def getById(id: Int)(implicit s: Session)  = {
    val results = discography.filter(_.id === id)
    results.list
  }

  def delete(id: Int)(implicit s: Session) = {
    val toDelete = discography.filter(_.id === id)
    toDelete.delete
  }

  def deleteByName(name: String)(implicit s: Session) = {
    val toDelete = discography.filter(_.name === name)
    toDelete.delete
  }

  def getDDL = { discography.ddl }
  def getByReleaseType(releaseType: Int)(implicit s: Session)  = {
    val results = discography.filter(_.releaseType === releaseType)

    results.list
  }

  val discographyForm: Form[Discography] = Form {
    mapping (
      "id" -> number,
      "name" -> text,
      "releaseType" -> number,
      "imagePath" -> text
    )(Discography.apply)(Discography.unapply _)
  }
}

case class Track(id: Int,relID: Int, position: Int, title: String)

object Track{
  class TracksTable(tag:Tag) extends Table[Track](tag,"tracks"){
    def id  = column[Int]("ID", O.PrimaryKey,O.AutoInc)
    def relId = column[Int]("RELEASE_ID")
    def position = column[Int]("POSITION")
    def title = column[String]("NAME")

    def * = (id,relId,position,title) <> ((Track.apply _).tupled,Track.unapply)

    def release = foreignKey("REL_FK",relId,Discography.discography)(_.id)


  }

  val tracks = TableQuery[TracksTable]

  def get(implicit s: Session) = { tracks.list }

  def insert(track :Track)(implicit s: Session) = { tracks.insert(track) }

  def deleteByRelId(relid: Int)(implicit s: Session) = { tracks.filter(_.relId === relid).delete }

  def getById(id: Int)(implicit s: Session)  = {
    val results = tracks.filter(_.id === id)
    results.list
  }

  def getByReleaseId(relId: Int)(implicit s: Session ) ={
    val results = tracks.filter(_.relId === relId)
    results.list
  }
}