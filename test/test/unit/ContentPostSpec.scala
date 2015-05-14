package test.unit

import java.text.{SimpleDateFormat, DateFormat}
import java.util.Date

import com.daoostinboyeez.git.GitRepo
import data.Content
import models.{ContentTypeMap, Biography, Discography, ContentPost}
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.db.DB
import play.api.test.FakeApplication
import play.api.test.Helpers._
import test._
import test.helpers.PostHelper

import scala.slick.jdbc.JdbcBackend._

class ContentPostSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter {

  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo, withGlobal = Some(TestGlobal))


  def database = Database.forDataSource(DB.getDataSource())
  "Content Post" must {



    "Be able to insert and retrieve posts items" in {
      database.withSession { implicit session =>

        val newsItem = PostHelper.createPost("DOB Test News Post","MC Donalds","News Content for db spec",1)
        val nonNewsItem =  PostHelper.createPost( "DOB Test Music Post","MC Donalds","Some cool DoB Music for dbspec",2)
        val result = Content.get
        result.head mustEqual newsItem
      }
    }
    "Be able to retrieve posts ordered by creation date in reverse order" in {
      database.withSession{ implicit session =>

        PostHelper.createPost("Post 1","MC Donalds","Post 1",1,"")
        PostHelper.createPost("Post 2","MC Donalds","Post 2",1,"")
        PostHelper.createPost("Post 3","MC Donalds","Post 3",1,"")
        PostHelper.createPost("Post 4","MC Donalds","Post 4",1,"")
        val mostRecent = PostHelper.createPost("Post 6","MC Donalds","Post 5",1,"")
        val post = Content.getByDate.head
        post.dateCreated mustEqual mostRecent.dateCreated
      }
    }

    "Be able to retrieve posts ordered by creation date in reverse order and filter by type" in {
      database.withSession{ implicit session =>

        PostHelper.createPost("Post 32","MC Donalds","Post 3",1,"")
        val mostRecentType1 = PostHelper.createPost("Post 62","MC Donalds","Post 5",1,"")
        PostHelper.createPost("Post 43","MC Donalds","Post 4",2,"")
        val post = Content.getByDate(1).head
        post mustEqual mostRecentType1
      }
    }

    "Be able to retrieve posts created before a particular date ordered by creation date in reverse order" in {
      database.withSession{ implicit session =>

        PostHelper.createPost("Post 21","MC Donalds","Post rwe1",1,"")
        PostHelper.createPost("Post 42","MC Donalds","Post rew2",1,"")
        PostHelper.createPost("Post 53","MC Donalds","Post ewr3",1,"")
        PostHelper.createPost("Post 44","MC Donalds","Postfs 4",1,"")
        val target = PostHelper.createPost("Post 65","MC Donalds","Postfsd 5",1,"")
        PostHelper.createPost("Post 6ewq5","MC Donalds","Postewqeqwfsd 5",1,"","20170803154423")
        val df = new SimpleDateFormat("yyyyMMddHHmmss")
        val targetDate = df.parse("20170802154423")

        val post = Content.getByDate(targetDate).head

        post mustEqual target
      }
    }

    "Be able to retrieve posts created before a particular date ordered by creation date in reverse order filtered by type" in {
      database.withSession{ implicit session =>

        PostHelper.createPost("Post 21","MC Donalds","wet rwe1",1,"")
        PostHelper.createPost("Post 42423","MC Donalds","23 rew2",1,"")
        PostHelper.createPost("Post 342","MC Donalds","43 ewr3",1,"")
        val target = PostHelper.createPost("Post 432","MC Donalds","432 4",2,"")
        PostHelper.createPost("Post 65","MC Donalds","wrw rwewe",1,"")
        PostHelper.createPost("Post 6ewq5","MC Donalds","Postewqeqwfsd 5",1,"","20170803154423")
        PostHelper.createPost("Postdsf 6ewq5","MC Donalds","fdsfsdfds 5",2,"","20170803154423")
        val df = new SimpleDateFormat("yyyyMMddHHmmss")
        val targetDate = df.parse("20170802154423")

        val post = Content.getByDate(2,targetDate).head

        post mustEqual target
      }
    }

    "Be able to limit number of items retrieved by type" in {
      database.withSession{ implicit session =>

        PostHelper.createPost("Post 215","MC Donalds","Jimbo Jambo 1",1,"")
        PostHelper.createPost("Post 216","MC Donalds","Jimbo Jambo 2",1,"")
        PostHelper.createPost("Post 217","MC Donalds","Jimbo Jambo 3",1,"")
        PostHelper.createPost("Post 218","MC Donalds","Jimbo Jambo 4",1,"")
        PostHelper.createPost("Post 219","MC Donalds","Jimbo Jambo 5",2,"")

        val posts = Content.getByDate(1,3)

        posts must have length(3)
      }
    }

    "Be able to limit number of items retrieved by type before date" in {
      database.withSession{ implicit session =>

        PostHelper.createPost("Post 215","MC Donalds","Jimbo Jambo 1",1,"")
        PostHelper.createPost("Post 216","MC Donalds","Jimbo Jambo 2",1,"")
        PostHelper.createPost("Post 217","MC Donalds","Jimbo Jambo 3",1,"")
        PostHelper.createPost("Post 218","MC Donalds","Jimbo Jambo 4",1,"")
        val latest = PostHelper.createPost("Post 219","MC Donalds","Jimbo Jambo 5",1,"")
        PostHelper.createPost("Post 220","MC Donalds","Jimbo Jambo 6",1,"","20170803154423")

        val df = new SimpleDateFormat("yyyyMMddHHmmss")
        val targetDate = df.parse("20170802154423")
        val posts = Content.getByDate(1,targetDate,3)

        posts must have length(3)
        posts.head.dateCreated mustEqual latest.dateCreated
      }
    }

    "Not retrieve drafts by default" in {
      database.withSession { implicit session =>

        val draft = PostHelper.createDraft("Draft Post", "MC Donalds", "Leeek 1", 1, "")
        PostHelper.createPost("Post 2166", "MC Donalds", "Leeek 2", 1, "")
        PostHelper.createPost("Post 2133", "MC Donalds", "Leeek 3", 1, "")

        val posts = Content.getByDate(1, new Date(), 10)
        posts must not contain draft
      }
    }

    "Have a way of  retrieving drafts" in {
      database.withSession { implicit session =>

        val draft = PostHelper.createDraft("Draft Post", "MC Donalds", "Leeek 1", 1, "")
        PostHelper.createPost("Post 2166", "MC Donalds", "Leeek 2", 1, "")
        PostHelper.createPost("Post 2133", "MC Donalds", "Leeek 3", 1, "")

        val posts = Content.getByDateWithDrafts(1, new Date(), 10)
        posts must contain (draft)
      }
    }

    "Be able to retrieve tags" in {
      database.withSession { implicit session =>
        val post = PostHelper.createPostWithTags("Post with tags","Some Content",ContentTypeMap("News"),"Leek,Freek,Gimp")
        post.tags must contain allOf ("Leek","Gimp","Freek")

      }
    }


    "Tidy this up" in pending
  }


  before{
    val repo = GitRepo.apply()
    repo.refresh
  }


}