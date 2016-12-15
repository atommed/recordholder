package service.components

import models.Track
import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

/**
  * Created by gregory on 08.12.16.
  */
trait TracksComponent extends AlbumsComponent with ArtistsComponent {
  this: HasDatabaseConfigProvider[JdbcProfile] =>

  import driver.api._

  class Tracks(tag: Tag) extends Table[Track](tag, "tracks")  {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def title = column[String]("title")

    def originalName = column[String]("original_name")

    def extension = column[String]("extension")

    def length = column[Double]("length")

    def bitrate = column[Long]("bitrate")

    def hasCover = column[Boolean]("has_cover")

    def artistId = column[Option[Long]]("artist_id")

    def albumId = column[Option[Long]]("album_id")

    def artist = foreignKey("artist_fk", artistId, TableQuery[Artists])(_.id.?)

    def album = foreignKey("album_fk", albumId, TableQuery[Albums])(_.id.?)

    def * = (
      id,
      title,
      originalName,
      extension,
      length,
      bitrate,
      hasCover,
      artistId,
      albumId) <> (Track.tupled, Track.unapply)
  }

}
