package test.integration.pages

import java.io.File

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.{Page, WebBrowser}
import test.helpers.SeleniumHelper

/**
 * Created by andrew on 17/12/14.
 */
class BiographyPage(val port: Int)(implicit driver:WebDriver) extends Page with WebBrowser {
  val url = s"localhost:$port/biography"

  def biographyDivs = {
    cssSelector("*[id*='bioDiv']").findAllElements
  }

  def biographyImages = cssSelector("*[id*='bioImage']").findAllElements

  def biographyImage(id:String) = cssSelector(s"#bioImage${id}").findElement.get

  def biographyDetails(id: String) = {
    cssSelector("#bioText" + id).findElement.get.text
  }

  def saveButton(id: String) = cssSelector("#bioSave" + id).findElement

  def saveButtons = cssSelector("*[id*='bioSave']").findAllElements

  def editButtons = cssSelector("*[id*='bioEdit']").findAllElements

  def updateBio(id: String, text: String) = {
    val textBox = cssSelector("#bioText" + id).findElement.get
    textBox.underlying.sendKeys(text)
  }



  def updateImage(bioId:String, file: String) = {
    val imageId = s"bioImageDiv${bioId}"
    SeleniumHelper.fileUpload(new java.io.File(file),imageId)
  }

  def saveBio(id: String) = {
    click on cssSelector("#bioSave" + id)
  }

  def saveSuccessfulVisible(id: String) = {
    cssSelector("#bioSuccess" + id).element.isDisplayed
  }

  def saveFailureVisible(id: String) = {
    cssSelector("#bioFailure" + id).element.isDisplayed
  }

  def nameEditable(id: String) = {
    cssSelector("#bioName" + id).element.attribute("contenteditable").getOrElse("false") match {
      case "true" => true
      case _ => false
    }
  }

  def bioEditable(id: String) = {
    cssSelector("#bioText" + id).element.attribute("contenteditable").getOrElse("false") match {
      case "true" => true
      case _ => false
    }
  }

  def imageEditable(id: String) = {
    cssSelector("#bioImageDiv" + id).element.attribute("contenteditable").getOrElse("false") match {
      case "true" => true
      case _ => false
    }
  }

  def clickOnEditButton(id: String) = {
    click on cssSelector("#bioEdit" + id)
  }
}