/**
 * Created by andrew on 25/01/15.
 */
package controllers


import jp.t2v.lab.play2.auth.AuthElement
import models.{UserAccount}
import models.UserRole.Administrator
import play.api.libs.json.Json._
import play.api.mvc.Controller

class AuthJsonApi extends Controller with AuthElement with StandardAuthConfig {


}
