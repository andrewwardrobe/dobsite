package test.integration.pages

import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.Select
import org.scalatest.selenium.WebBrowser
import play.api.Logger

import scala.collection.mutable.ListBuffer

/**
 * Created by andrew on 01/03/15.
 */
class PostPage(val port: Int)(implicit driver:WebDriver) extends org.scalatest.selenium.Page with WebBrowser {
  val url = s"localhost:$port/post"

  def articleText = {
    val box = cssSelector("div[id=editor]").findElement.get
    box.text
  }

  def post(id:String) = s"localhost:$port/post/$id"

  def postType = cssSelector("#postType").findElement.get.underlying.getAttribute("value")

  def title = cssSelector("#postTitle").findElement.get.text

  def author = cssSelector("#author").findElement.get.text

  def dateCreated = cssSelector("#dateCreated").findElement.get.text


}