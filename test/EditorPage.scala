import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser

import scala.collection.mutable.ListBuffer

/**
 * Created by andrew on 01/03/15.
 */
class EditorPage(val port: Int)(implicit driver:WebDriver) extends org.scalatest.selenium.Page with WebBrowser {
  val url = s"localhost:$port/editor"

  def editorBoxText = {
    val box = cssSelector("div[id=editor]").findElement.get
    box.text
  }

  def revisionList = {
    val revisions: ListBuffer[String] = new ListBuffer[String]()
    cssSelector("*[id*='revId']").findAllElements.toList.foreach { element =>
      revisions += element.attribute("value").get.toString
    }
    revisions.toList
  }

  def save = {
    submit()
  }


}
