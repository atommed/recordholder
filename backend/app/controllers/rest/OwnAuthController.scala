package controllers.rest

import javax.inject.Inject

import com.google.inject.Singleton

import scala.concurrent.Future
import scala.util.{Failure, Success}
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import service.OwnAuthService

/**
  * Created by gregory on 05.12.16.
  */
@Singleton
class OwnAuthController @Inject()(protected val authService: OwnAuthService) extends Controller{
  private implicit val credentialsR = Json.reads[service.OwnAuthService.Credentials]

  def signIn = Action.async(parse.json){implicit req=>
    credentialsR.reads(req.body).asOpt match {
      case Some(credentials) => authService.signIn(credentials) map {
        case Some(u) => Ok(u.name)
        case None => BadRequest(s"Can't sign in as ${credentials.login}")
      }
      case None => Future.successful(BadRequest("Can't parse request"))
    }
  }

  def signUp = Action.async(parse.json){implicit req=>
    credentialsR.reads(req.body).asOpt match {
      case Some(credentials) => authService.register(credentials) map {
        case Success(u) => Ok(u.name)
        case Failure(e) => BadRequest(e.getMessage)
      }
      case None => Future.successful(BadRequest("Can't parse request"))
    }
  }
}
