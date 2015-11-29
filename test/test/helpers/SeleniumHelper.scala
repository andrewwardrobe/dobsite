package test.helpers

import java.io.File

import org.openqa.selenium.{WebDriver, By, JavascriptExecutor}

import scala.util.Random

/**
  * Created by andrew on 29/11/15.
  */
object SeleniumHelper {
  def fileUpload(file: File, target: String)(implicit driver: WebDriver): AnyRef = {
    val jsDriver = driver.asInstanceOf[JavascriptExecutor]
    val rand = new Random()
    val inputId = target + "FileUpload" //+ rand.nextInt()
    //Not a fan of using  this method
    jsDriver.executeScript("var element =  document.getElementById(\""+inputId+"\"); if (element != null ) {element.parentElement.removeChild(element);}")
    jsDriver.executeScript(inputId + " = window.$('<input id=\"" + inputId + "\" type=\"file\"/>').appendTo('body');");
    driver.findElement(By.id(inputId)).sendKeys(file.getAbsolutePath)
    jsDriver.executeScript(s"e = $$.Event('drop'); e.originalEvent = {dataTransfer : { files : " + inputId + s".get(0).files } }; $$('#${target}').trigger(e); console.log(JSON.stringify(e));")
    jsDriver.executeScript("var element =  document.getElementById(\""+inputId+"\"); if (element != null ) {element.parentElement.removeChild(element);}")
  }
}
