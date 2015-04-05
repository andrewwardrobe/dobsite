package test.integration.pages

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.{Page, WebBrowser}

/**
 * Created by andrew on 17/12/14.
 */
class BiographyListPage(val port: Int)(implicit driver:WebDriver) extends Page with WebBrowser {
  val url = s"localhost:$port/biography"
  def biographyDivs = { cssSelector("*[id*='bioDiv']").findAllElements }
  def biographyImages = cssSelector("*[id*='bioImage']").findAllElements


  def biographyDetails(id : Int) = { cssSelector("#bioText"+ id).findElement.get.text }
  def saveButton(id:Int) = cssSelector("#bioSave"+id).findElement
  def saveButtons = cssSelector("*[id*='bioSave']").findAllElements
  def editButtons = cssSelector("*[id*='bioEdit']").findAllElements
  def viewBiography(name: String) = {
      val bios = cssSelector("td > a[id*='bioText']").findAllElements
      val link = bios.filter( a => a.text.contains(name)).next()
      click on link
      new BiographyDetailsPage(port)(driver)
  }

  def updateBio(id: Int, text: String) = {
    val textBox = cssSelector("#bioText"+ id).findElement.get
    textBox.underlying.sendKeys(text)
  }

  def saveBio(id: Int) = {
    click on cssSelector("#bioSave"+id)
  }

  def saveSuccessfulVisible(id:Int) = {
    cssSelector("#bioSuccess"+id).element.isDisplayed
  }

  def saveFailureVisible(id:Int) = {
    cssSelector("#bioFailure"+id).element.isDisplayed
  }

  def nameEditable(id:Int) = {
    cssSelector("#bioName"+id).element.attribute("contenteditable").getOrElse("false")  match {
      case "true" => true
      case _ => false
    }
  }

  def bioEditable(id:Int) = {
    cssSelector("#bioText"+id).element.attribute("contenteditable").getOrElse("false")  match {
      case "true" => true
      case _ => false
    }
  }
  def imageEditable(id:Int) = {
    cssSelector("#bioImageDiv"+id).element.attribute("contenteditable").getOrElse("false") match {
      case "true" => true
      case _ => false
    }
  }

  def clickOnEditButton(id:Int) ={
    click on cssSelector("#bioEdit"+id)
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