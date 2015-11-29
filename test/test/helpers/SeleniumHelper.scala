package test.helpers

import java.io.File

import org.openqa.selenium.{WebDriver, By, JavascriptExecutor}

/**
  * Created by andrew on 29/11/15.
  */
object SeleniumHelper {
  def fileUpload(file: File, target: String)(implicit driver: WebDriver): AnyRef = {
    val jsDriver = driver.asInstanceOf[JavascriptExecutor]
    val inputId = target + "FileUpload"
    jsDriver.executeScript(inputId + " = window.$('<input id=\"" + inputId + "\" type=\"file\"/>').appendTo('body');");
    driver.findElement(By.id(inputId)).sendKeys(file.getAbsolutePath)
    jsDriver.executeScript(s"e = $$.Event('drop'); e.originalEvent = {dataTransfer : { files : " + inputId + s".get(0).files } }; $$('#${target}').trigger(e);")
  }
}
