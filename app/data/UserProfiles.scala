package data

import models.Profile

/**
 *
 * Created by andrew on 01/08/15.
 *
 */
object UserProfiles extends DAOBase[Profile]("profiles"){
  import models.JsonFormats._
}
