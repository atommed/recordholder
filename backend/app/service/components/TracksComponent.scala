package service.components

import models.Track
import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

/**
  * Created by gregory on 08.12.16.
  */
trait TracksComponent {
  this: HasDatabaseConfigProvider[JdbcProfile] =>

  import driver.api._

  class Tracks(tag: Tag) extends Table[Track](tag, "tracks") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def title = column[String]("title")

    def originalName = column[String]("original_name")

    def extension = column[String]("extension")

    def length = column[Float]("length")

    def bitrate = column[Int]("bitrate")

    def hasCover = column[Boolean]("has_cover")

    def artistId = column[Long]("artist_id")

    def albumId = column[Long]("album_id")

    def * = (
      id.?,
      title,
      originalName,
      extension,
      length,
      bitrate,
      hasCover,
      artistId.?,
      albumId.?) <> (Track.tupled, Track.unapply)
  }

}
