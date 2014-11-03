package models

import models._
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._


/**
 * Created by andrew on 26/08/14.
 */
case class Person(name: String,age: String)


object Person {

  class PersonTable(tag: Tag) extends Table[Person](tag, "person") {
    //Pri Key
    def name = column[String]("name",O.PrimaryKey)
    def age = column[String]("age")
    def * = (name, age) <> ((Person.apply _).tupled, Person.unapply)
  }

  val people = TableQuery[PersonTable] //Read Slixk computers database

  def get(implicit s: Session) = { people.list }

  def insert(person: Person)(implicit s: Session) = { people.insert(person) }

  def getByName(name: String)(implicit s: Session)  = {
    val results = people.filter(_.name === name)

    results.list
  }

  val personForm: Form[Person] = Form {
    mapping (
      "name" -> text,
      "age" -> text
    )(Person.apply)(Person.unapply _)
  }

}
