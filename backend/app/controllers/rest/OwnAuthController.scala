package controllers.rest

import javax.inject.{Inject, Singleton}

import models.User
import play.api.libs.json.Json
import play.api.mvc._
import service.OwnAuthService

import scala.util.{Failure, Success}
import util.WebAuth.LogIn


@Singleton
class OwnAuthController @Inject()(protected val authService: OwnAuthService) extends Controller {
  private implicit val credentialsR = Json.reads[service.OwnAuthService.Credentials]
  private implicit val credentialsW = Json.writes[User]

  private val CantParse = BadRequest("Can't parse request")

  def signIn = Action(parse.json) { implicit req =>
    credentialsR.reads(req.body).asOpt match {
      case Some(credentials) =>
        authService.signIn(credentials) map LogIn getOrElse BadRequest(s"Can't sign in as ${credentials.login}")
      case None => CantParse
    }
  }

  def signUp = Action(parse.json) { implicit req =>
    credentialsR.reads(req.body).asOpt match {
      case Some(credentials) =>
        authService.register(credentials) match {
          case Success(u) =>
            LogIn(u)
          case Failure(OwnAuthService.UserExistsException(login)) =>
            BadRequest(s"User $login already exists")
        }
      case None => CantParse
    }
  }
}
