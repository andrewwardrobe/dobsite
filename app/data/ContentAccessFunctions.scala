package data

import java.util.Date

import models.ContentPost
import play.api.libs.json.Json

import scala.collection.mutable.ListBuffer

/**
 * Created by andrew on 14/05/15.
 */
trait ContentAccessFunctions {this: ContentPostSchema =>
  
  import play.api.db.slick.Config.driver.simple._
  val postTable = TableQuery[PostTable]


  def get(implicit s: Session) = { postTable.list }
  def getById(id: String)(implicit s: Session) = { postTable.filter(_.id === id).list }
  def getByTitle(title: String)(implicit s: Session) = { postTable.filter(_.title.toLowerCase === title.toLowerCase).list }
  def getByType(typ: Int)(implicit s: Session) = { postTable.filter(_.postType === typ).list }


  def getLiveContentByUserLatestFirst(userId:Int)(implicit s: Session)  = {
    postTable.filter( post => post.userId === userId
    && post.isDraft === false).sortBy(_.dateCreated.desc).list
  }

  def getDraftContentByUserLatestFirst(userId:Int)(implicit s: Session)  = {
    postTable.filter( post => post.userId === userId
      && post.isDraft === true).sortBy(_.dateCreated.desc).list
  }

  //Does this belong here? cut it be moved to a trait?
  def getTags(id:String)(implicit s:Session) = {
    Tags.contentTags.filter(_.postId === id).flatMap(_.tagsFK).list
  }

  def getTagsAsJson(id:String)(implicit s:Session) = {
    val lb = new ListBuffer[String]
    getTags(id).foreach { t =>
      lb += t.title
    }
    Json.toJson(lb.toList)
  }

  def getXNewsItemsFromId(id: String, max: Int)(implicit s: Session) = {
    getXItemsFromId(id,max,1)
  }


  def getXItemsFromId(id: String, max: Int, typ: Int)(implicit s: Session) = {
    postTable.filter( blogPost =>
      blogPost.id <= id &&
        blogPost.postType === typ
    ).sortBy(_.id.desc).take(5).list
  }



  def getXNewsItems(max: Int)(implicit s: Session) = {
    getXItems(1,max)
  }

  def getXItems(typ: Int,max: Int)(implicit s: Session) = {
    postTable.filter( blogPost =>
      blogPost.postType === typ
    ).sortBy(_.id.desc).take(max).list
  }



  def getNews(implicit s: Session) = { postTable.filter(_.postType === 1).list }

  def insert(newsItem: ContentPost)(implicit s: Session) = {
    postTable.insert(newsItem)
    newsItem.id
  }

  def getByDate(implicit  s: Session) = { postTable.filter(post => post.isDraft === false).sortBy(_.dateCreated.desc).list}

  def getByDate(typ: Int)(implicit  s: Session) = {
    postTable.filter(post => post.postType === typ && post.isDraft === false ).sortBy(_.dateCreated.desc).list
  }

  def getByDate(typ: Int, max :Int)(implicit  s: Session) = {
    postTable.filter(post => post.postType === typ && post.isDraft === false).sortBy(_.dateCreated.desc).take(max).list
  }

  def getByDate(beforeDate: Date)(implicit  s: Session) = {
    postTable.filter(post => post.dateCreated < beforeDate && post.isDraft === false).sortBy(_.dateCreated.desc).list
  }

  def getByDate(beforeDate: Date, max :Int)(implicit  s: Session) = {
    postTable.filter(post => post.dateCreated < beforeDate && post.isDraft === false).sortBy(_.dateCreated.desc).take(max).list
  }



  def getByDate(typ: Int,beforeDate: Date )(implicit  s: Session) = {
    postTable.filter( post =>
      post.dateCreated < beforeDate
        && post.postType === typ
        && post.isDraft === false
    ).sortBy(_.dateCreated.desc).list
  }

  def getByDate(typ: Int,beforeDate: Date,max :Int )(implicit  s: Session) = {
    postTable.filter( post =>
      post.dateCreated < beforeDate
        && post.postType === typ
        && post.isDraft === false
    ).sortBy(_.dateCreated.desc).take(max).list
  }

  def getByDateWithDrafts(implicit  s: Session) = { postTable.sortBy(_.dateCreated.desc).list}

  def getByDateWithDrafts(typ: Int)(implicit  s: Session) = {
    postTable.filter(post => post.postType === typ ).sortBy(_.dateCreated.desc).list
  }

  def getByDateWithDrafts(typ: Int, max :Int)(implicit  s: Session) = {
    postTable.filter(post => post.postType === typ).sortBy(_.dateCreated.desc).take(max).list
  }

  def getByDateWithDrafts(beforeDate: Date)(implicit  s: Session) = {
    postTable.filter(post => post.dateCreated < beforeDate).sortBy(_.dateCreated.desc).list
  }

  def getByDateWithDrafts(beforeDate: Date, max :Int)(implicit  s: Session) = {
    postTable.filter(post => post.dateCreated < beforeDate).sortBy(_.dateCreated.desc).take(max).list
  }

  def delete(id:String)(implicit  s: Session) = postTable.filter(_.id === id).delete

  def deleteAll(implicit  s: Session) = postTable.delete

  def getByDateWithDrafts(typ: Int,beforeDate: Date )(implicit  s: Session) = {
    postTable.filter( post =>
      post.dateCreated < beforeDate
        && post.postType === typ
    ).sortBy(_.dateCreated.desc).list
  }

  def getByDateWithDrafts(typ: Int,beforeDate: Date,max :Int )(implicit  s: Session) = {
    postTable.filter( post =>
      post.dateCreated < beforeDate
        && post.postType === typ
    ).sortBy(_.dateCreated.desc).take(max).list
  }

  def clearAll(implicit  s: Session) = { postTable.delete }
  def update(post :ContentPost)(implicit s: Session) = { postTable.insertOrUpdate(post) }
}
