package controllers

import javax.inject.Inject

import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._
import service.OwnAuthService

import scala.concurrent.Future
import scala.util.{Failure, Success}

class Application @Inject()(authService: OwnAuthService) extends Controller {

  def register(login: String, password: String) = Action.async(implicit req=>
    authService register(login, password) map{
      case Success(u) => Ok(u.name)
      case Failure(e) => BadRequest(e.getMessage)
    }
  )

  def signIn(login: String, password: String) = Action.async(implicit req=>
    authService.signIn(login, password) map {
      case Some(u) => Ok(u.name)
      case None => BadRequest(s"Can't log in as $login")
    }
  )

  private implicit val credentialsReads = (
    (JsPath \ "login").read[String] and (JsPath \ "password").read[String])(
    (_, _)
  )

  def signInJSON = Action.async(parse.json){implicit req=>
    Json.fromJson[Tuple2[String, String]](req.body).asEither match {
      case Right((login, password)) =>
        authService.signIn(login, password) map {
          case Some(u) => Ok(u.name)
          case None => BadRequest(s"Can't sign in as $login")
        }
      case Left(_) => Future.successful(BadRequest("U bad man"))
    }
  }

  def index(name:String) = Action.async{implicit req=>
    val rp = authService.register(name, "bleat")
    rp map {
      case Success(u) => Ok(u.name)
      case Failure(e) => BadRequest(e.getMessage)
    }
  }
}
