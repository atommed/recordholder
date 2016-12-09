package service.components

import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

trait UserArtistsComponent extends UsersComponent with ArtistsComponent {
  this: HasDatabaseConfigProvider[JdbcProfile] =>
  import driver.api._

  class UserArtists(tag: Tag) extends Table[(Long, Long)](tag, "user_artists"){
    def userId = column[Long]("user_id")
    def artistId = column[Long]("artist_id")

    def * = (userId, artistId)

    def user = foreignKey("user_fk", userId, TableQuery[Users])(_.id)
    def artist = foreignKey("artist_fk", artistId, TableQuery[Artists])(_.id)
  }
}
