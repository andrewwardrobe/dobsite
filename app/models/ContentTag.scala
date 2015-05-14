package models


import data.{ContentToTagSchema, ContentToTagFunctions}
import models._
import play.api._
import play.api.db.slick._


case class ContentTag(id:String,title: String)







case class ContentToTagLink(postId :String, tagID :String)


