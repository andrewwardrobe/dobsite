package test.helpers

import java.util.UUID

import data.{UserProfiles, UserAccounts}
import models._
import play.api.db.DB
import play.api.Play.current
import reactivemongo.bson.BSONObjectID
import scala.slick.jdbc.JdbcBackend._
import models.JsonFormats._
/**
 * Created by andrew on 03/04/15.
 */
object UserAccountHelper {

  def database = Database.forDataSource(DB.getDataSource())

  def createUser(userId: String, email :String, password: String, role: String) = {
      val user = new UserAccount(BSONObjectID.generate, email, password, userId, role)
      UserAccounts.create(user)
      user
  }


  def createUser(userId: String, password: String, role: String) = {
      val user = new UserAccount(BSONObjectID.generate, s"$userId@daoostinboyeez.com", password, userId, role)
      UserAccounts.create(user)
      user
  }

  def createUserAndProfile(userId: String, password: String, role: String) = {
    val user = new UserAccount(BSONObjectID.generate, s"$userId@daoostinboyeez.com", password, userId, role)
    UserAccounts.create(user)
    val profile = new Profile(BSONObjectID.generate, user._id,"Some Test about","assets/images/crew/donalds_bw.jpg",None)
    UserProfiles.insert(profile)
    user
  }

  def createUserWithAlias(userId: String, email :String, password: String, role: String,alias: String ) = {
    val user = new UserAccount(BSONObjectID.generate, s"$userId@daoostinboyeez.com", password, userId, role)
    UserAccounts.create(user)
    val profile = new Profile(BSONObjectID.generate, user._id,"Some Test about","assets/images/crew/donalds_bw.jpg",Some(List(alias)))
    UserProfiles.insert(profile)
    user
  }

  def createProfile(userId:BSONObjectID, about:String,avatar:String) = {
    val profile = new Profile(userId,BSONObjectID.generate,about,avatar,None)
    UserProfiles.insert(profile)
    profile
  }
}
