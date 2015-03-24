package test.integration.pages

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser

/**
 * Created by andrew on 17/12/14.
 */
class DiscographyPage (val port: Int)(implicit driver:WebDriver) extends org.scalatest.selenium.Page with WebBrowser {
  val url = s"localhost:$port/discography"

  def Albums = cssSelector("*[id*='alb']").findAllElements
  def Modals = cssSelector("*[id*='RelIDmodal']").findAllElements
  def Tracks = cssSelector("*[id*='_tab_cell']").findAllElements
}
