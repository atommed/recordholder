package service

import javax.inject.{Inject, Singleton}

import models.{OwnAuth, User}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import service.OwnAuthService.Credentials
import service.components.{OwnAuthsComponent, UsersComponent}
import slick.driver.JdbcProfile
import util.AuthUtils

import scala.concurrent.Future
import scala.util.Try

@Singleton()
class OwnAuthService @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with OwnAuthsComponent
    with UsersComponent {

  import driver.api._

  private val users = TableQuery[Users]
  private val ownAuths = TableQuery[OwnAuths]

  def register(credentials: Credentials): Future[Try[User]] = {
    val (login, password) = (credentials.login, credentials.password)
    val salt = AuthUtils.genSalt()
    val hash = AuthUtils.genHash(salt, password)
    db run (for {
      uId <- users returning users.map(_.id) += User(login)
      _ <- ownAuths += OwnAuth(Some(uId), login, hash, salt)
    } yield User(login, Some(uId))).asTry.transactionally
  }

  def signIn(credentials: Credentials): Future[Option[User]] = {
    val (login, password) = (credentials.login, credentials.password)
    val userQ = for {
      auth <- ownAuths if auth.login === login.bind
      user <- users if user.id === auth.userId
    } yield (user, auth.passwordHash, auth.salt)
    db.run(userQ.result.headOption) map {
      case Some((u, realHash, salt)) =>
        val providedHash = AuthUtils.genHash(salt, password)
        if (providedHash sameElements realHash) Some(u) else None
      case _ => None
    }
  }
}

object OwnAuthService {

  case class Credentials(login: String, password: String)

}