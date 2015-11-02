package controllers

/**
 * Created by andrew.g.wardrobe on 02/11/2015.
 */

import java.io.File

import jp.t2v.lab.play2.auth.OptionalAuthElement
import org.apache.commons.mail.EmailAttachment
import play.api.libs.mailer._
import javax.inject.Inject

import play.api.mvc.{Controller, Action}

class EmailController @Inject() (mailerClient: MailerClient) extends Controller  with OptionalAuthElement with StandardAuthConfig{
  def sendMail = StackAction { implicit request =>
    val cid = "1234"
    val email = Email(
      "Simple email",
      "Mister FROM <from@email.com>",
      Seq("Miss TO <to@email.com>"),
      // adds attachment
      attachments = Seq(
        AttachmentFile("attachment.pdf", new File("/some/path/attachment.pdf")),
        // adds inline attachment from byte array
        AttachmentData("data.txt", "data".getBytes, "text/plain", Some("Simple data"), Some(EmailAttachment.INLINE)),
        // adds cid attachment
        AttachmentFile("image.jpg", new File("/some/path/image.jpg"), contentId = Some(cid))
      ),
      // sends text, HTML or both...
      bodyText = Some("A text message"),
      bodyHtml = Some(s"""<html><body><p>An <b>html</b> message with cid <img src="cid:$cid"></p></body></html>""")
    )
    mailerClient.send(email)
    Ok("Check Logs")
  }
}