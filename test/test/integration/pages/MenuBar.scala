package test.integration.pages

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser

/**
 * Created by andrew on 01/03/15.
 */
class MenuBar(val port: Int)(implicit driver:WebDriver) extends org.scalatest.selenium.Page with WebBrowser {
    val url = s"localhost:$port/"

    def revisionsMenu = { cssSelector("#editorMenu").findElement.get }



}
