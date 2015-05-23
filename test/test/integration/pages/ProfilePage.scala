package test.integration.pages

import org.openqa.selenium.{WebElement, WebDriver}
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

  def avatarDisplayed ={
    val avatar = cssSelector("#avatar").findElement.get.underlying
    imageDisplayed(avatar)
  }

  def imageDisplayed(image:WebElement) = {
    val script = """
      |return arguments[0].complete && typeof arguments[0].naturalWidth != "undefined" && arguments[0].naturalWidth > 0
    """.stripMargin

    val result = executeScript(script,image)
    result  match {
      case loaded:java.lang.Boolean => loaded
      case _ => false
    }
  }
}
