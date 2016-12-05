package service.components

import models.OwnAuth
import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

trait OwnAuthsComponent {
  this: HasDatabaseConfigProvider[JdbcProfile] =>

  import driver.api._

  class OwnAuths(tag: Tag) extends Table[OwnAuth](tag, "own_auths") {
    def * = (userId.?, login, passwordHash, salt) <> (OwnAuth.tupled, OwnAuth.unapply)

    def userId = column[Int]("user_id")

    def login = column[String]("login", O.PrimaryKey)

    def passwordHash = column[Array[Byte]]("password_hash")

    def salt = column[Array[Byte]]("salt")
  }

}