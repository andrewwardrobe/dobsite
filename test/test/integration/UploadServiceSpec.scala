package test.integration

import java.io.{FileReader, File}
import java.nio.file.{Path, Files}

import controllers.{Authorised, routes}
import org.apache.commons.io.FileUtils
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.http.Writeable
import play.api.i18n.{MessagesApi, DefaultLangs, DefaultMessagesApi}

import play.api.libs.Files.TemporaryFile
import org.apache.http.entity.mime.content.{ ContentBody, FileBody }
import play.api.mvc.{Request, MultipartFormData}
import play.api.mvc.MultipartFormData.FilePart
import play.api.test.{FakeRequest, FakeApplication, FakeHeaders}
import play.api.test.Helpers._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._



/**
  * Created by andrew on 08/11/15.
  */
class UploadServiceSpec extends PlaySpec with OneServerPerSuite with ScalaFutures with MockitoSugar{

  "Upload Service" must {
    "Allow authorized users to upload images" in {
      //Todo: extract this into a helper
      FileUtils.copyFile(new File("public/images/jew.png"),new File("/tmp/file.jpg"))
      val tempFile = TemporaryFile(new File("/tmp/file.jpg"))
      val part = FilePart[TemporaryFile](key = "image", filename = "file", contentType = Some("image/jpeg"), ref = tempFile)
      val formData = MultipartFormData(dataParts = Map(), files = Seq(part), badParts = Seq(), missingFileParts = Seq())
      val request = mock[Request[MultipartFormData[TemporaryFile]]]
      when(request.body).thenReturn(formData)
      val controller = new Authorised(app.injector.instanceOf[MessagesApi])
      val result = controller.doUpload(request)
      status (result) mustBe OK

    }
  }
}
