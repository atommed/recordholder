package service.components

import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

/**
  * Created by gregory on 09.12.16.
  */
trait UserAlbumsComponent extends UsersComponent with AlbumsComponent {
  this: HasDatabaseConfigProvider[JdbcProfile] =>
  import driver.api._

  class UserAlbums(tag: Tag) extends Table[(Long, Long)](tag, "user_albums"){
    def userId = column[Long]("user_id")
    def albumId = column[Long]("album_id")

    def * = (userId, albumId)

    def user = foreignKey("user_fk", userId, TableQuery[Users])(_.id)
    def album = foreignKey("album_fk", albumId, TableQuery[Albums])(_.id)
  }
}
