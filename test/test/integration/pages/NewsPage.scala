package test.integration.pages

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser

import scala.collection.mutable.ListBuffer

class NewsPage(val port: Int)(implicit driver:WebDriver) extends org.scalatest.selenium.Page with WebBrowser {
  val url = s"localhost:$port/news"

  def Items = {
    val itemList: ListBuffer[String] = new ListBuffer[String]()
    cssSelector("div[id*='itemId']").findAllElements.toList.foreach { element =>
      itemList += element.text
    }
    itemList.toList
  }


  def itemLinks = {
   cssSelector("*[id*='itemLink']").findAllElements.toList
  }
  def TypeIds = {

    val typeIDList: ListBuffer[String] = new ListBuffer[String]()
    cssSelector("*[id*='typId']").findAllElements.toList.foreach { element =>
      typeIDList += element.attribute("value").get.toString
    }
    typeIDList.toList
  }
}
