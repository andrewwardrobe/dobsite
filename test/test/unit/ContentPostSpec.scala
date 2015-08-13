package test.unit

import java.text.{SimpleDateFormat, DateFormat}
import java.util.Date

import com.daoostinboyeez.git.GitRepo
import data.{ContentQueries, Content}
import models.{ContentTypeMap}
import org.hamcrest.core.AnyOf
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.db.DB
import play.api.test.FakeApplication
import play.api.test.Helpers._
import test._
import test.helpers.{UserAccountHelper, ContentHelper}


class ContentPostSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfter with ScalaFutures{

  import models.JsonFormats._
  implicit override lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase() ++ TestConfig.withTempGitRepo ++ TestConfig.withEmbbededMongo, withGlobal = Some(EmbedMongoGlobal))

  "Content Post" must {



    "Be able to insert and retrieve posts items" in {
        val newsItem = ContentHelper.createPost("DOB Test News Post","MC Donalds","News Content for db spec",1,None)
        val nonNewsItem =  ContentHelper.createPost( "DOB Test Music Post","MC Donalds","Some cool DoB Music for dbspec",2,None)
        val result = Content.findById(newsItem.id).futureValue
        result.head mustEqual newsItem
    }

    "Be able to retrieve posts ordered by creation date.coffee in reverse order" in {
        ContentHelper.createPost("Post 1","MC Donalds","Post 1",1,"",None)
        ContentHelper.createPost("Post 2","MC Donalds","Post 2",1,"",None)
        ContentHelper.createPost("Post 3","MC Donalds","Post 3",1,"",None)
        ContentHelper.createPost("Post 4","MC Donalds","Post 4",1,"",None)
        val mostRecent = ContentHelper.createPost("Post 6","MC Donalds","Post 5",1,"",None)
        val post = Content.find(ContentQueries.dateReverse).futureValue.head
        post.dateCreated mustEqual mostRecent.dateCreated

    }

    "Be able to retrieve posts ordered by creation date.coffee in reverse order and filter by type" in {
      ContentHelper.createPost("Post 32","MC Donalds","Post 3",1,"",None)
      ContentHelper.createPost("Post 43","MC Donalds","Post 4",2,"",None)
      val mostRecentType1 = ContentHelper.createPost("Post 62","MC Donalds","Post 5",1,"",None)
      val post = Content.find(ContentQueries.dateReverse).futureValue.head
      post mustEqual mostRecentType1

    }

    "Be able to retrieve posts created before a particular date.coffee ordered by creation date.coffee in reverse order" in {
      ContentHelper.createPost("Post 21","MC Donalds","Post rwe1",1,"",None)
      ContentHelper.createPost("Post 42","MC Donalds","Post rew2",1,"",None)
      ContentHelper.createPost("Post 53","MC Donalds","Post ewr3",1,"",None)
      ContentHelper.createPost("Post 44","MC Donalds","Postfs 4",1,"",None)
      ContentHelper.createPost("Post 65","MC Donalds","Postfsd 5",1,"",None)
      val target = ContentHelper.createPost("Post 6ewq5","MC Donalds","Postewqeqwfsd 5",1,"","20170803154423",None)
      val df = new SimpleDateFormat("yyyyMMddHHmmss")
      val targetDate = df.parse("20170802154423")

      val post = Content.find(ContentQueries.dateReverse).futureValue.head

      post mustEqual target
    }

    "Be able to retrieve posts created before a particular date.coffee ordered by creation date.coffee in reverse order filtered by type" in {

        ContentHelper.createPost("Post 21","MC Donalds","wet rwe1",1,"",None)
        ContentHelper.createPost("Post 42423","MC Donalds","23 rew2",1,"",None)
        ContentHelper.createPost("Post 342","MC Donalds","43 ewr3",1,"",None)
        ContentHelper.createPost("Post 432","MC Donalds","432 4",2,"",None)
        val target = ContentHelper.createPost("Post 65","MC Donalds","wrw rwewe",1,"",None)
        ContentHelper.createPost("Post 6ewq5","MC Donalds","Postewqeqwfsd 5",1,"","20170803154423",None)
        ContentHelper.createPost("Postdsf 6ewq5","MC Donalds","fdsfsdfds 5",2,"","20170803154423",None)
        val df = new SimpleDateFormat("yyyyMMddHHmmss")
        val targetDate = df.parse("20170802154423")

        val post = Content.find(ContentQueries.dateReverse(targetDate)).futureValue.head

        post mustEqual target

    }

    "Be able to limit number of items retrieved by type" in {


        ContentHelper.createPost("Post 215","MC Donalds","Jimbo Jambo 1",1,"",None)
        ContentHelper.createPost("Post 216","MC Donalds","Jimbo Jambo 2",1,"",None)
        ContentHelper.createPost("Post 217","MC Donalds","Jimbo Jambo 3",1,"",None)
        ContentHelper.createPost("Post 218","MC Donalds","Jimbo Jambo 4",1,"",None)
        ContentHelper.createPost("Post 219","MC Donalds","Jimbo Jambo 5",2,"",None)

        val posts = Content.find(ContentQueries.dateReverse,3).futureValue

        posts must have length(3)

    }

    "Be able to limit number of items retrieved by type before date.coffee" in {
        ContentHelper.createPost("Post 215","MC Donalds","Jimbo Jambo 1",1,"",None)
        ContentHelper.createPost("Post 216","MC Donalds","Jimbo Jambo 2",1,"",None)
        ContentHelper.createPost("Post 217","MC Donalds","Jimbo Jambo 3",1,"",None)
        ContentHelper.createPost("Post 218","MC Donalds","Jimbo Jambo 4",1,"",None)
        val latest = ContentHelper.createPost("Post 219","MC Donalds","Jimbo Jambo 5",1,"",None)
        ContentHelper.createPost("Post 220","MC Donalds","Jimbo Jambo 6",1,"","20170803154423",None)

        val df = new SimpleDateFormat("yyyyMMddHHmmss")
        val targetDate = df.parse("20170802154423")
        val posts = Content.find(ContentQueries.dateReverse(targetDate),3).futureValue

        posts must have length(3)
        posts.head.dateCreated mustEqual latest.dateCreated
    }

    "Be able to limit number of items retrieved by type before date.coffee when retrieving by author" in {
      ContentHelper.createPost("Post 215","MC Donalds","Jimbo Jambo 1",1,"",None)
      ContentHelper.createPost("Post 216","MC Donalds","Jimbo Jambo 2",1,"",None)
      ContentHelper.createPost("Post 217","Test Author","Jimbo Jambo 3",1,"",None)
      ContentHelper.createPost("Post 218","MC Donalds","Jimbo Jambo 4",1,"",None)
      ContentHelper.createPost("Post 219","MC Donalds","Jimbo Jambo 5",1,"",None)
      val latest = ContentHelper.createPost("Post 220","MC Donalds","Jimbo Jambo 6",1,"","20170803154423",None)

      val df = new SimpleDateFormat("yyyyMMddHHmmss")
      val targetDate = df.parse("20170802154423")
      val posts = Content.find(ContentQueries.liveContentByAuthorBeforeDate("MC Donalds",1,targetDate),3).futureValue

      posts must have length(3)
      posts must not contain latest
    }

    "Not retrieve drafts by default" in {
      val draft = ContentHelper.createDraft("Draft Post", "MC Donalds", "Leeek 1", 1, "",None)
      ContentHelper.createPost("Post 2166", "MC Donalds", "Leeek 2", 1, "",None)
      ContentHelper.createPost("Post 2133", "MC Donalds", "Leeek 3", 1, "",None)

      val posts = Content.find(ContentQueries.dateReverse(new Date),10).futureValue
      posts must not contain draft

    }

    "Have a way of  retrieving drafts" in {
      val draft = ContentHelper.createDraft("Draft Post", "MC Donalds", "Leeek 1", 1, "",None)
      ContentHelper.createPost("Post 2166", "MC Donalds", "Leeek 2", 1, "",None)
      ContentHelper.createPost("Post 2133", "MC Donalds", "Leeek 3", 1, "",None)

      val posts = Content.find(ContentQueries.dateReverseWithDrafts(new Date),10).futureValue
      posts must contain (draft)
    }

    "Be able to retrieve tags" in {
        val post = ContentHelper.createPostWithTags("Post with tags","Some Content",ContentTypeMap("Blog"),"Leek,Freek,Gimp",None)
        post.tags.get must contain allOf ("Leek","Gimp","Freek")
    }

    "Be able to retrieve posts by user" in {

        val user = UserAccountHelper.createUserAndProfile("TestUser","TestUser","TrustedContributor")
        val authorPost = ContentHelper.createPost("Test Post Leeeek","MC Donalds","Twatous Leek us",1,"",Some(user._id))
        val draftPost = ContentHelper.createDraft("Draftus Post Leeeek","MC Donalds","Twatous Leek us",1,"",Some(user._id))
        val nonAuthorPost = ContentHelper.createPost("Test Post Leeeek 2","MC Donalds","Twatous Leek us",1,"",None)

        val posts = Content.find(ContentQueries.liveContentByUserLatestFirst(user.id)).futureValue
        posts must (contain (authorPost) and contain noneOf(nonAuthorPost,draftPost))

    }

    "Be able to retrieve posts by author" in {
      val user = UserAccountHelper.createUserAndProfile("TestUser","TestUser","TrustedContributor")
      val authorPost = ContentHelper.createPost("Test Post Leeeek","MC Donalds","Twatous Leek us",1,"",Some(user._id))
      val nonAuthorPost = ContentHelper.createPost("Test Post Leeeek","Gary The Baldy","Twatous Leek us",1,"",Some(user._id))
      val authorDraft = ContentHelper.createDraft("Test Post Leeeek","MC Donalds","Twatous Leek us",1,"",Some(user._id))
      val posts = Content.find(ContentQueries.liveContentByAuthorLatestFirst("MC Donalds")).futureValue
      posts must (contain (authorPost) and contain noneOf(nonAuthorPost,authorDraft))
    }

    "Be able to retrieve drafts by user" in {
      val user = UserAccountHelper.createUserAndProfile("TestUser","TestUser","TrustedContributor")
      val authorPost = ContentHelper.createPost("Test Post Leeeek","MC Donalds","Twatous Leek us",1,"",Some(user._id))
      val draftPost = ContentHelper.createDraft("Draftus Post Leeeek","MC Donalds","Twatous Leek us",1,"",Some(user._id))
      val nonAuthorPost = ContentHelper.createPost("Test Post Leeeek 2","MC Donalds","Twatous Leek us",1,"",None)

      val posts = Content.find(ContentQueries.draftContentByUserLatestFirst(user.id)).futureValue

      posts must (contain (draftPost) and contain noneOf(authorPost,nonAuthorPost))
    }

  }


  before{
    val repo = GitRepo.apply()
    repo.refresh
  }

  after{
      Content.deleteAll
  }


}