package test.integration.pages

import org.openqa.selenium.{WebElement, WebDriver}
import org.scalatest.selenium.{Page, WebBrowser}
import play.api.Logger

/**
 * Created by andrew on 17/12/14.
 */
class ProfilePage(val port: Int)(implicit driver:WebDriver) extends Page with WebBrowser {
  val url = s"localhost:$port/profile"

  def usernameText = {
    val userName = cssSelector("#userName").findElement.get
    userName.text
  }

  def toogleEditMode =  click on cssSelector("#editBtn")

  def aboutText = {
    cssSelector("#about").findElement.get.text
  }

  def aboutEditable = {
    cssSelector("#about").findElement match {
      case Some(element) => {
        element.attribute("contenteditable").getOrElse("false") match {
          case "true" => true
          case _ => false
        }
      }
      case _ => {
        false
      }

    }
  }

  def saveButton =  cssSelector("#saveBtn").findElement.get

  def save = click on cssSelector("#saveBtn")

  def saveSuccess =  cssSelector("#saveSuccess").findElement.get


  def avatarEditable = {
    cssSelector("#avatarDiv").findElement match {
      case Some(element) => {
        element.attribute("contenteditable").getOrElse("false") match {
          case "true" => true
          case _ => false
        }
      }
      case _ => {
        false
      }

    }


  }

  def postLinks = cssSelector("*[id*='postLink']").findAllElements

  def updateAbout(text:String) = {
    val about = cssSelector("#about").findElement.get.underlying
    about.sendKeys(text)
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
