package util

import models.User
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{RequestHeader, Result, Results}

object WebAuth {
  private val userIdSessionKey = "userId"
  def LogIn(u: User)(implicit rh: RequestHeader, wr: Writes[User]): Result =
    Results.Ok(Json.toJson(u)).withSession(userIdSessionKey -> u.id.toString)
  def getUserId(implicit rh: RequestHeader): Option[Long] = rh.session.get(userIdSessionKey).map(_.toLong)
}
