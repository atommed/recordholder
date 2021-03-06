package service

import java.sql.Connection
import javax.inject.{Inject, Singleton}

import anorm._
import models.User
import play.api.db.Database
import service.OwnAuthService.{Credentials, UserExistsException}
import util.AuthUtils
import scala.util.Try

@Singleton()
class OwnAuthService @Inject()(db: Database) {
  private def isUserExists(login: String)
                          (implicit c: Connection) = {
    SQL"""
          SELECT 1 FROM own_auth WHERE login = ${login}
      """
      .as(SqlParser.scalar[Int].singleOpt).nonEmpty
  }

  private def createUser(name: String)
                        (implicit c: Connection): Long = {
    SQL"""
          INSERT INTO app_user(name) VALUES(${name})
       """
      .executeInsert(SqlParser.scalar[Long].single)
  }

  private def addOwnUserAuth(userId: Long, login: String, hash: Array[Byte], salt: Array[Byte])
                            (implicit c: Connection) = {
    SQL"""
          INSERT INTO own_auth(user_id, login, password_hash, salt)
          VALUES(${userId}, ${login}, ${hash}, ${salt})
       """
      .executeInsert()
  }

  private def findUserCredentials(login: String)
                                 (implicit c: Connection) = {
    import SqlParser._
    SQL"""
          SELECT u.id, u.name, oa.password_hash, oa.salt
          FROM own_auth oa JOIN app_user u
          ON oa.user_id = u.id
          WHERE oa.login = ${login}
      """
      .as((long(1) ~ str(2) ~ byteArray(3) ~ byteArray(4) map flatten).singleOpt)
  }

  def register(credentials: Credentials): Try[User] = {
    val (login, password) = (credentials.login, credentials.password)
    val salt = AuthUtils.genSalt()
    val hash = AuthUtils.genHash(salt, password)
    Try(db.withTransaction(implicit c =>
      if (isUserExists(login))
        throw UserExistsException(login)
      else {
        val newUserId = createUser(login)
        addOwnUserAuth(newUserId, login, hash, salt)
        User(newUserId, login)
      }
    ))
  }

  def signIn(credentials: Credentials): Option[User] = {
    val (login, password) = (credentials.login, credentials.password)
    db.withConnection(implicit c =>
      findUserCredentials(login).flatMap({ case (uId, uName, realHash, salt) =>
        val actualHash = AuthUtils.genHash(salt, password)
        if (actualHash sameElements realHash)
          Some(User(uId, uName))
        else
          None
      })
    )
  }
}

object OwnAuthService {

  case class Credentials(login: String, password: String)

  case class UserExistsException(login: String) extends Exception(s"$login already exists in DB")

}