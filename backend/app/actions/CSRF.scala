package actions

import java.util.Base64

import play.api.mvc._
import util.ThreadLocalSecureRandom

import scala.concurrent.Future

case class CSRFCheck[A](action: Action[A]) extends Action[A] {
  lazy val parser = action.parser

  override def apply(request: Request[A]): Future[Result] = {
    (for {
      session <- request.session.get(CSRF.fieldName)
      header <- request.headers.get(CSRF.fieldName)
    } yield session == header) match {
      case Some(true) => action(request)
      case Some(false) =>
        Future.successful(Results.Unauthorized("Session and header csrf tokens don't match"))
      case None => Future.successful(Results.BadRequest(
        s"This request must contain '${CSRF.fieldName}' header and session")
      )
    }
  }
}

object CSRF {
  val fieldName = "csrf-token"
  val tokenLength = 128
  private val rnd = new ThreadLocalSecureRandom

  def genToken(): String = {
    Base64.getEncoder.encodeToString(rnd.nextBytes(tokenLength))
  }

  object Implicit {
    private val maxCookieAge = Some((1 << 31) - 1)

    implicit class AjaxCsrfProtector(r: Result)(implicit ev: RequestHeader) {
      def protect: Result = {
        val token = genToken()
        r
          .addingToSession(fieldName -> token)
          .withCookies(Cookie(fieldName, token, maxAge = maxCookieAge, httpOnly = false))
      }
    }

  }

}