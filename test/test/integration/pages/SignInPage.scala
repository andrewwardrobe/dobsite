package test.integration.pages

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser

/**
 * Created by andrew on 01/03/15.
 */
class SignInPage(val port: Int)(implicit driver:WebDriver) extends org.scalatest.selenium.Page with WebBrowser {
    val url = s"localhost:$port/login"

    def signin(name:String, password: String) = {
      go to url
      textField("email").value = name
      pwdField("password").value = password
      submit()
    }

    def signout = {
      go to s"localhost:$port/signout"
    }

}
