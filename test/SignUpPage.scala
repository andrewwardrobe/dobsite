import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser

/**
  * Created by andrew on 01/03/15.
  */
class SignUpPage(val port: Int)(implicit driver:WebDriver) extends org.scalatest.selenium.Page with WebBrowser {
      val url = s"localhost:$port/signup"
      def email = { textField("email").value
      }

      def email(email: String) = {
        textField("email").value = email
      }

      def signup(name : String, email: String, password:String) = {
        goTo(url)
        textField("name").value = name
        textField("email").value = email
        pwdField("password").value = password
        pwdField("confirm").value = password
        submit()
      }

 }
