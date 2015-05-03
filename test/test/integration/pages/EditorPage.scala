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
class EditorPage(val port: Int)(implicit driver:WebDriver) extends org.scalatest.selenium.Page with WebBrowser {
  val url = s"localhost:$port/editor"

  def editorBoxText = {
    val box = cssSelector("div[id=editor]").findElement.get
    box.text
  }

  def addContent(content: String) = {
    val box = cssSelector("div[id=editor]").findElement.get
    box.underlying.sendKeys(content)
  }

  def posttype(pt:String) = s"localhost:$port/editor/new/$pt"

  def post(id:String) = s"localhost:$port/editor/$id"



  def revisionListText = {
    val revisions: ListBuffer[String] = new ListBuffer[String]()
    cssSelector("*[id*='revId']").findAllElements.toList.foreach { element =>
      revisions += element.text
    }
    revisions.toList
  }

  def revisionList = cssSelector("*[id*='revId']").findAllElements.toList

  def revisionLinks = cssSelector("*[id*='revLink']").findAllElements.toList

  def toggleDraftMode = click on id("isDraft")

  def liveAlertVisible = cssSelector("#editAlertLive").element.isDisplayed

  def liveToDraftAlertVisible = cssSelector("#editAlertLive2Draft").element.isDisplayed

  def draftAlertVisible = cssSelector("#editAlertDraft").element.isDisplayed

  def draftToLiveAlertVisible = cssSelector("#editAlertDraft2Live").element.isDisplayed

  def unsavedAlertVisible = cssSelector("#editAlertUnsaved").element.isDisplayed

  def revisionAlertVisible = cssSelector("#editAlertRevision").element.isDisplayed

  def newAlertVisible = cssSelector("#editAlertNew").element.isDisplayed



  def draftMode = { val elemClass = id("isDraft").webElement.getAttribute("class")
    elemClass.contains("isDraftOn")
  }

  def save = {
    click on id("saveButton")
  }

  def postType = {
    val select = new Select(cssSelector("#postType").findElement.get.underlying)
    select.getFirstSelectedOption().getText()
  }

  def title = cssSelector("#postTitle").findElement.get.text


  def saveSuccessful = {
    cssSelector("#btnSuccessful").findElement.get.underlying.isDisplayed()
  }

  def highLightText(text:String) = { //Doesnt quite work

    val element = id("editor").webElement
    val actions = new Actions(driver)
    val leek= xpath(s"//*[text()[contains(.,'$text')]]").webElement
    val loc = leek.getLocation
    val size = leek.getSize()
    Logger.info(leek.getTagName())
    Logger.info("x: "+loc.x+ " ,y: "+loc.y)
    Logger.info("width: "+size.width+ " ,height: "+size.height)

    actions.moveToElement(tagName("body").webElement, loc.x, loc.y).clickAndHold().moveByOffset(size.width,size.height).release().perform
  }

}
