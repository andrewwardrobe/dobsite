package test.unit

import java.text.SimpleDateFormat

import com.daoostinboyeez.git.GitRepo

import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.db.DB
import play.api.test.FakeApplication
import play.api.test.Helpers._
import test._
import test.helpers.{ReactiveMongoApp, ContentHelper}


class ContentHelperSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter with ScalaFutures with ReactiveMongoApp {

  import scala.concurrent.duration.DurationInt


  implicit override lazy val app = buildAppEmbed


  "Content Helper" must {


    "Be able to create a post with a specifed date.coffee" in {
      val df = new SimpleDateFormat("yyyyMMddHHmmss")
      val expectedDate = "20150418163545"
      val inputDate = df.parse(expectedDate)
      val post = ContentHelper.createPost("Testing Dates", "MC Donalds", "Testing some dates", 1, "", inputDate, None)
      val actualDate = df.format(post.dateCreated)

      actualDate mustEqual expectedDate
    }


    "Be able to create a post with a specifed date.coffee string" in {
      val df = new SimpleDateFormat("yyyyMMddHHmmss")
      val expectedDate = "20150418163545"
      val post = ContentHelper.createPost("Testing Dates", "MC Donalds", "Testing some dates", 1, "", "20150418163545", None)
      val actualDate = df.format(post.dateCreated)

      actualDate mustEqual expectedDate
    }
  }

  before{
    val repo = GitRepo.apply()
    repo.refresh
  }
}