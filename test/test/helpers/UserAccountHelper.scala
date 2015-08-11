package test.helpers

import java.util.UUID

import data.{Profiles, UserAccounts}
import models._
import play.api.db.DB
import play.api.Play.current
import reactivemongo.bson.BSONObjectID
import scala.concurrent.Await
import scala.slick.jdbc.JdbcBackend._
import models.JsonFormats._
/**
 * Created by andrew on 03/04/15.
 */
object UserAccountHelper {

  import scala.concurrent.duration.DurationInt
  def database = Database.forDataSource(DB.getDataSource())

  def createUser(userId: String, email :String, password: String, role: String) = {
      val user = new UserAccount(BSONObjectID.generate, email, password, userId, role)
      Await.result(UserAccounts.create(user),10 seconds)
      user
  }


  def createUser(userId: String, password: String, role: String) = {
      val user = new UserAccount(BSONObjectID.generate, s"$userId@daoostinboyeez.com", password, userId, role)
      Await.result(UserAccounts.create(user),10 seconds)
      user
  }

  def createUserAndProfile(userId: String, password: String, role: String, about : String) = {
    val user = new UserAccount(BSONObjectID.generate, s"$userId@daoostinboyeez.com", password, userId, role)
    Await.result(UserAccounts.create(user),10 seconds)
    val profile = new Profile(BSONObjectID.generate, user._id,about,"assets/images/crew/donalds_bw.jpg",None)
    Await.result(Profiles.insert(profile),10 seconds)
    user
  }

  def createUserAndProfile(userId: String, password: String, role: String) : UserAccount = {
    createUserAndProfile(userId, password, role, "Some Test About")
  }

  def createUserWithAlias(userId: String, email :String, password: String, role: String, alias: String, about :String) = {
    val user = new UserAccount(BSONObjectID.generate, s"$userId@daoostinboyeez.com", password, userId, role)
    Await.result(UserAccounts.create(user),10 seconds)
    val profile = new Profile(BSONObjectID.generate, user._id,about,"assets/images/crew/donalds_bw.jpg",Some(List(alias)))
    Await.result(Profiles.insert(profile),10 seconds)
    user
  }

  def createUserWithAlias(userId: String, email :String, password: String, role: String,alias: String ) : UserAccount = {
    createUserWithAlias(userId, email , password, role, alias, "Some Test About")
  }

  def createProfile(userId:BSONObjectID, about:String,avatar:String) = {
    val profile = new Profile(BSONObjectID.generate,userId,about,avatar,None)
    Await.result(Profiles.insert(profile),10 seconds)
    profile
  }
}
