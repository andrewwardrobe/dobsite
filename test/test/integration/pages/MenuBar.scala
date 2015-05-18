package test.integration.pages

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser

import scala.collection.mutable.ListBuffer

/**
 * Created by andrew on 01/03/15.
 */
class MenuBar(val port: Int)(implicit driver:WebDriver) extends org.scalatest.selenium.Page with WebBrowser {

    val url = s"localhost:$port/"

    def revisionsMenu = { cssSelector("#editorMenu").findElement.get }

    def revisionList = cssSelector("*[id*='revId']").findAllElements.toList

    def revisionLinks = cssSelector("*[id*='revLink']").findAllElements.toList

    def userMenu = id("userMenu")

    def clickOnUserMenu = click on id("userMenu")

    def profileLink = cssSelector("#profileLink").findElement.get

    def editLinks = {
        val links = new ListBuffer[String]
        cssSelector("*[id*='editLink']").findAllElements.toList.foreach { element =>
            links += element.text
        }
        links.toList
    }
    def clickEditLink(link:String) = {
        click on id("addContent")
        click on cssSelector("#editLink"+link)
    }
}
