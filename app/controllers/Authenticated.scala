package controllers

import jp.t2v.lab.play2.auth.{AuthenticationElement}
import play.api.mvc.Controller

/**
 * Created by andrew on 29/12/14.
 */
object Authenticated extends Controller with AuthenticationElement with AuthConfigImpl {

}
