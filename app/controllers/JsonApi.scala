package controllers



import models._
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.json.Json._


/**
 * Created by andrew on 07/09/14.
 */
object JsonApi extends Controller {

  implicit val discoFormat =  Json.format[Discography]
  implicit val trackFormat =  Json.format[Track]
  implicit val bioFormat =  Json.format[Biography]
  implicit val newsFormat =  Json.format[Blog]

  def getDiscographyByReleaseType(_type: Int) = DBAction { implicit response =>
      Ok(toJson(Discography.getByReleaseType(_type)))
  }

  def getNews = DBAction { implicit response =>
    Ok(toJson(Blog.getNews))
  }

  def getNewsById(id: Int) = DBAction { implicit response =>
    Ok(toJson(Blog.getById(id)))
  }

  def getNewsByRange(min: Int, max: Int) = DBAction { implicit response =>
    Ok(toJson(Blog.getNewsByRange(min,max)))
  }

  def getDiscography = DBAction { implicit response =>
    Ok(toJson(Discography.get))
  }

  def getRelease(id: Int) = DBAction { implicit response =>
    Ok(toJson(Discography.getById(id)))
  }

  def getTracksByReleaseId(relId: Int) = DBAction {implicit response =>
    Ok(toJson(Track.getByReleaseId(relId)))

  }

  def getBioByType(bioType: Int) = DBAction { implicit response =>
    Ok(toJson(Biography.getByType(bioType)))
  }


  def getBioById(id: Int) = DBAction { implicit response =>
    Ok(toJson(Biography.getByType(id)))
  }

  def getBiography() = DBAction { implicit response =>
     Ok(toJson(Biography.getAll))
  }
}
