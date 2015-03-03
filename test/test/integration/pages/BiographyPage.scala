package test.integration.pages

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.{Page, WebBrowser}

/**
 * Created by andrew on 17/12/14.
 */
class BiographyListPage(val port: Int)(implicit driver:WebDriver) extends Page with WebBrowser {
  val url = s"localhost:$port/biography"
  def biographyLinks = { cssSelector("td > a[id*='bioLink']").findAllElements }
  def biographyImages = cssSelector("td > img[id*='bioImage']").findAllElements
  def biographyCells = cssSelector("td[id*='bioCell']").findAllElements
  def viewBiography(name: String) = {
      val bios = cssSelector("td > a[id*='bioLink']").findAllElements
      val link = bios.filter( a => a.text.contains(name)).next()
      click on link
      new BiographyDetailsPage(port)(driver)
  }
}

class BiographyDetailsPage(val port: Int)(implicit driver:WebDriver) extends Page with WebBrowser {
  val url = s"localhost:$port/biography"
  def text = {
    val biographyDiv = cssSelector("*[id*='bioText']").findElement
    biographyDiv.get.text
  }
  def image = cssSelector("*[id*='bioText']").findElement
}