package controllers.rest

import javax.inject.{Inject, Singleton}

import actions.CSRFCheck
import models.User
import org.postgresql.util.PSQLException
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._
import service.OwnAuthService

import scala.concurrent.Future
import scala.util.{Failure, Success}


@Singleton
class OwnAuthController @Inject()(protected val authService: OwnAuthService) extends Controller {

  import actions.CSRF.Implicit.AjaxCsrfProtector

  private implicit val credentialsR = Json.reads[service.OwnAuthService.Credentials]
  private implicit val credentialsW = Json.writes[User]

  def testCsrf = CSRFCheck {
    Action {
      Ok("lel")
    }
  }

  def signIn = Action.async(parse.json) { implicit req =>
    credentialsR.reads(req.body).asOpt match {
      case Some(credentials) => authService.signIn(credentials) map {
        case Some(u) => LogIn(u)
        case None => BadRequest(s"Can't sign in as ${credentials.login}")
      }
      case None => Future.successful(BadRequest("Can't parse request"))
    }
  }

  def LogIn(u: User)(implicit rh: RequestHeader) = {
    Ok(Json.toJson(u))
      .withSession("userId" -> u.id.toString)
      .protect
  }

  def signUp = Action.async(parse.json) { implicit req =>
    credentialsR.reads(req.body).asOpt match {
      case Some(credentials) => authService.register(credentials) map {
        case Success(u) => LogIn(u)
        case Failure(e: PSQLException) if e.getSQLState == "23505" =>
          BadRequest("User already exists")
        case Failure(e) => InternalServerError("Something happaned")
      }
      case None => Future.successful(BadRequest("Can't parse request"))
    }
  }
}
