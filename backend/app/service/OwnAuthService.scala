package service

import java.sql.Connection
import javax.inject.{Inject, Singleton}

import anorm._
import models.User
import play.api.db.Database
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import service.OwnAuthService.{Credentials, UserExistsException}
import util.AuthUtils
import util.DBUtils.Implicit.FuturedDb

import scala.concurrent.Future
import scala.util.Try

@Singleton()
class OwnAuthService @Inject()(db: Database) {
  private def isUserExists(login: String)
                        (implicit c: Connection) =
    SQL"""
          SELECT 1 FROM own_auth WHERE login = $login
      """
      .as(SqlParser.scalar[Int].singleOpt).nonEmpty

  private def createUser(name: String)
                        (implicit c: Connection): Long =
    SQL"""
          INSERT INTO app_user(name) VALUES($name)
       """
      .executeInsert(SqlParser.scalar[Long].single)

  private def addOwnUserAuth(userId: Long, login: String, hash: Array[Byte], salt: Array[Byte])
                            (implicit c: Connection)=
    SQL"""
          INSERT INTO own_auth(user_id, login, password_hash, salt)
          VALUES($userId, $login, $hash, $salt)
       """
    .executeInsert()

  private def findUserCredentials(login: String)
                                 (implicit c: Connection) = {
    import SqlParser._
    val parser = for{
      a <- long("id")
      b <- str("name")
      c <- byteArray("password_hash")
      d <- byteArray("salt")
    } yield (a,b,c,d)
    import  SqlParser.{scalar=>s, flatten}
    SQL"""
          SELECT u.id, u.name, oa.password_hash, oa.salt
          FROM own_auth oa JOIN app_user u
          ON oa.user_id = u.id
          WHERE oa.login = $login
      """
      .as(parser.singleOpt)
  }

  def register(credentials: Credentials): Future[User] = {
    val (login, password) = (credentials.login, credentials.password)
    val salt = AuthUtils.genSalt()
    val hash = AuthUtils.genHash(salt, password)

    db.futureTransaction({implicit conn=>
      if(isUserExists(login))
        throw UserExistsException(login)
      val newUserId = createUser(login)
      addOwnUserAuth(newUserId, login, hash, salt)
      User(newUserId, login)
    })
  }

  def signIn(credentials: Credentials): Future[Option[User]] = {
    val (login, password) = (credentials.login, credentials.password)
    db.futureQuery(implicit c=>findUserCredentials(login)).map(_.flatMap({
      case (userId, userName, realHash, salt) =>
        val genSalt = AuthUtils.genHash(salt, password)
        if(genSalt sameElements realHash)
          Some(User(userId, userName))
        else
          None
    }))
  }
}

object OwnAuthService {
  case class Credentials(login: String, password: String)
  case class UserExistsException(login: String) extends Exception(s"$login already exists in DB")
}