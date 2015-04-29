package test.integration.pages

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser

/**
 * Created by andrew on 01/03/15.
 */
class MenuBar(val port: Int)(implicit driver:WebDriver) extends org.scalatest.selenium.Page with WebBrowser {
    val url = s"localhost:$port/"

    def revisionsMenu = { cssSelector("#editorMenu").findElement.get }

    def revisionList = cssSelector("*[id*='revId']").findAllElements.toList

    def revisionLinks = cssSelector("*[id*='revLink']").findAllElements.toList

    def editLinks = cssSelector("*[id*='editLink']").findAllElements.toList


}
