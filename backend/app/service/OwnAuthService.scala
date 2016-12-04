package service

import javax.inject.{Inject, Singleton}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future
import service.components.{OwnAuthsComponent, UsersComponent}
import models.{OwnAuth, User}
import util.AuthUtils

import scala.util.{Random, Success, Try}


@Singleton()
class OwnAuthService @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with OwnAuthsComponent
    with UsersComponent
{
  import driver.api._
  private val users = TableQuery[Users]
  private val ownAuths = TableQuery[OwnAuths]

  case class Credentials(login: String, password: String)

  def register(login: String, password: String) : Future[Try[User]] = {
    val salt = AuthUtils.genSalt()
    val hash = AuthUtils.genHash(salt, password)
    db run (for {
      uId <- users returning users.map(_.id) += User(login)
      _ <- ownAuths += OwnAuth(Some(uId), login, hash, salt)
    } yield User(login, Some(uId))).asTry.transactionally
  }

  def signIn(login: String, password: String) : Future[Option[User]] = {
    val userQ = for{
      auth <- ownAuths if auth.login === login.bind
      user <- users if user.id === auth.userId
    } yield (user, auth.passwordHash, auth.salt)
    db.run(userQ.result.headOption) map {
      case Some((u, realHash, salt)) =>
        val providedHash = AuthUtils.genHash(salt, password)
        if(providedHash sameElements realHash) Some(u) else None
      case _ => None
    }
  }
}
