package service

import javax.inject.Inject

import anorm.SqlParser.{bool, double, long, str}
import anorm._
import models.{Album, Artist, Track}
import play.api.db.Database

class TrackCollectionService @Inject()(private val db: Database) {
  def findUserAlbums(userId : Long) : Seq[Album]=  db.withConnection {implicit conn=>
    import SqlParser._
    val parser = for {
      id <- long("album.id")
      artistId <- long("album.artist_id").?
      name <- str("album.name")
      description <- str("album.description").?
      coverId <- long("album.cover_id").?
    } yield Album(id, artistId, name, description, coverId)
    SQL"""
          SELECT album.id, album.artist_id, album.name, album.description, album.cover_id
          FROM user_albums JOIN album ON user_albums.album_id = album.id
          WHERE user_albums.user_id = ${userId}
      """.as(parser.*)
  }

  def findUserArtists(userId: Long) : Seq[Artist] = db.withConnection {implicit conn=>
    import SqlParser._
    val parser = for {
      id <- long("artist.id")
      name <- str("artist.name")
      description <- str("artist.description").?
    } yield Artist(id, name, description)
    SQL"""
          SELECT artist.id, artist.name, artist.description
          FROM user_artists JOIN artist ON user_artists.artist_id = artist.id
          WHERE user_artists.user_id = ${userId}
      """.as(parser.*)
  }

  private val trackParser = for{
    id <- long("id")
    title <- str("title")
    originalName <- str("original_name")
    extension <- str("extension")
    length <- double("length")
    bitrate <- long("bitrate")
    hasCover <- bool("has_cover")
  } yield Track(id, title, originalName, extension, length, bitrate, hasCover, None, None)

  def findAlbumTracks(albumId: Long): Seq[Track] = db.withConnection {implicit conn=>
    SQL"""
         SELECT * FROM track where album_id = ${albumId}
       """.as(trackParser.*)
  }

  def findeUnknownAlbumTracks(userId: Long) : Seq[Track] = db.withConnection(implicit conn=>{
    SQL"""
         SELECT * FROM user_tracks JOIN track ON user_tracks.track_id = track.id WHERE user_id =${userId}
       """.as(trackParser.*)
  })
}
