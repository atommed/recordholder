package service

import javax.inject.Inject

import anorm._
import models.{Album, Artist}
import play.api.db.Database

class TrackCollectionService @Inject()(private val db: Database) {
  def findUserAlbums(userId : Long) : Seq[Album]=  db.withConnection {implicit conn=>
    import SqlParser._
    val parser = for {
      id <- long("album.id")
      artistId <- long("album.artist_id").?
      name <- str("name")
      description <- str("description").?
    } yield Album(id, artistId, name, description)
    SQL"""
          SELECT album.id, album.artist_id, album.name, album.description
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
          WHERE user_albums.user_id = ${userId}
      """.as(parser.*)
  }
}
