package service.components

import models.Album
import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

/**
  * Created by gregory on 09.12.16.
  */
trait AlbumsComponent extends ArtistsComponent{
  this: HasDatabaseConfigProvider[JdbcProfile] =>
  import driver.api._
  class Albums(tag: Tag) extends Table[Album](tag, "albums"){
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def artistId = column[Option[Long]]("artist_id")
    def name = column[String]("name")
    def description = column[Option[String]]("description")

    def artist = foreignKey("artist_fk", artistId, TableQuery[Artists])(_.id.?)

    def * = (id, artistId, name, description) <> (Album.tupled, Album.unapply)
  }
}
