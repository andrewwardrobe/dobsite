package models


import data.{PostToTagSchema, PostToTagFunctions}
import models._
import play.api._
import play.api.db.slick._


case class ContentTag(id:String,title: String)







case class PostToTagLink(postId :String, tagID :String)


