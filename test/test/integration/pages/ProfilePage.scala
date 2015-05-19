package test.integration.pages

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.{Page, WebBrowser}

/**
 * Created by andrew on 17/12/14.
 */
class ProfilePage(val port: Int)(implicit driver:WebDriver) extends Page with WebBrowser {
  val url = s"localhost:$port/profile"

  def usernameText = {
    val userName = cssSelector("#userName").findElement.get
    userName.text
  }

  def about = {
    cssSelector("#about").findElement.get.text
  }
}
