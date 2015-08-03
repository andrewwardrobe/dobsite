package test.unit

import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.Helpers._
import play.api.test.{FakeApplication, FakeRequest}
import reactivemongo.bson.BSONObjectID
import test.{TestConfig, TestGlobal}

import scala.util.{Failure, Success}

/**
 * Created by andrew on 14/09/14.
 */
class BSONMapperSpec extends PlaySpec{

  def bsonMapper(id:String) = {
    val bson = BSONObjectID.parse(id)
    bson match {
      case Success(obj) => obj
      case Failure(ex) => BSONObjectID.generate
    }
  }

  "Mapper" should {


    "Transform a BSON Object" in {
      val bson =  BSONObjectID.parse("Leek")
      bson.isSuccess mustEqual  true
    }



    def setup = {

    }
  }

}
