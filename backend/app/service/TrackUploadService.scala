package service

import java.io.File
import java.nio.file.{Files, Paths}
import java.sql.Connection
import javax.inject.{Inject, Singleton}

import anorm.SqlParser.scalar
import anorm._
import models.{Album, Artist, Track}
import play.api.Configuration
import play.api.db.Database
import util.MetadataRetriever

import scala.collection.JavaConverters.mapAsScalaMapConverter

@Singleton
class TrackUploadService @Inject()(db: Database, conf: Configuration) {
  private val storage = Paths.get(conf.getString("storage").get)
  private val tracksPath = storage.resolve("tracks")
  private val coversPath = storage.resolve("covers")
  private val analyzerExecutable = Paths.get(conf.getString("ffmpeg-metadata-analyzer.executable").get)
  private val analyzer = new ThreadLocal[MetadataRetriever] {
    override def initialValue(): MetadataRetriever = new MetadataRetriever(analyzerExecutable)
  }

  private def getExtension(possibleExtensions: Array[String]): String = {
    val preferableExtensions = "ogg" :: "flac" :: "mp3" :: "m4a" :: Nil
    val matchedPreferable = for {
      extension <- preferableExtensions
      if possibleExtensions.contains(extension)
    } yield extension
    matchedPreferable.headOption match {
      case Some(extension) => extension;
      case None => possibleExtensions(0)
    }
  }

  private def addTrack(userId: Long, t: Track)(implicit c: Connection): Long = {
    val newTrackId =
      SQL"""
          INSERT INTO track
          (title, original_name, extension, length, bitrate, has_cover, artist_id, album_id)
          VALUES
          (${t.title}, ${t.originalName}, ${t.extension}, ${t.length}, ${t.bitrate}, ${t.hasCover}, ${t.artistId}, ${t.albumId})
       """.executeInsert(scalar[Long].single)
    SQL"INSERT INTO user_tracks(user_id, track_id) VALUES(${userId}, ${newTrackId})".execute()
    newTrackId
  }

  private def findAlbum(userId: Long, name: String)(implicit c: Connection) = {
    import SqlParser._
    val parser = for {
      id <- long("id")
      artistId <- long("artist_id").?
      name <- str("name")
      description <- str("description").?
      coverId <- long("cover_id").?
    } yield Album(id, artistId, name, description, coverId)
    SQL"""
         SELECT id, artist_id, name, description, cover_id
         FROM user_albums JOIN album ON user_albums.user_id = album.id
         WHERE user_albums.user_id = ${userId} AND album.name = ${name}
         ORDER BY album.artist_id NULLS FIRST
         LIMIT 1
       """.as(parser.singleOpt)
  }

  private def findArtist(userId: Long, name: String)(implicit c: Connection) = {
    import SqlParser._
    val parser = for {
      id <- long("id")
      name <- str("name")
      description <- str("description").?
    } yield Artist(id, name, description)
    SQL"""
         SELECT id, name, description
         FROM user_artists JOIN artist ON user_artists.user_id = artist.id
         WHERE user_artists.user_id = ${userId} AND artist.name = ${name}
         LIMIT 1
       """.as(parser.singleOpt)
  }

  private def findAlbumArtist(userId: Long, albumName: String, artistName: String)(implicit c: Connection) = {
    import SqlParser._
    val parser = for {
      albumId <- long("album.id")
      artistId <- long("artist.id")
    } yield (albumId, artistId)
    SQL"""
         SELECT album.id, artist.id
         FROM user_albums ua
         JOIN album ON ua.album_id = album.id
         JOIN artist ON album.artist_id = artist.id
         WHERE ua.user_id=${userId}
         AND album.name = ${albumName}
         AND artist.name= ${artistName}
       """.as(parser.singleOpt)
  }

  private def addArtist(userId: Long, name: String)(implicit c: Connection) = {
    val artistId =
      SQL"""
            INSERT INTO artist(name) VALUES($name)
         """
        .executeInsert(scalar[Long].single)
    SQL"""
         INSERT INTO user_artists(user_id, artist_id) VALUES (${userId}, $artistId)
       """.executeInsert()
    Artist(artistId, name, None)
  }

  private def addAlbum(userId: Long, name: String, artistId: Option[Long] = None)(implicit c: Connection) = {
    val albumId =
      SQL"""
           INSERT INTO album(name, artist_id) VALUES(${name}, ${artistId})
         """
        .executeInsert(scalar[Long].single)
    SQL"""
         INSERT INTO user_albums(user_id, album_id) VALUES(${userId}, ${albumId})
       """
      .executeInsert()
    Album(albumId, None, name, None, None)
  }

  private def addCoverToAlbum(albumId: Long, coverId: Long)(implicit c: Connection) = {
    SQL"""
         UPDATE album SET cover_id = ${coverId} WHERE id=${albumId} and cover_id IS NULL
       """.executeUpdate()
  }

  private def getArtistOrCreate(userId: Long, name: String)(implicit c: Connection): Artist =
    findArtist(userId, name) getOrElse {
      addArtist(userId, name)
    }

  def getAlbumOrCreate(userId: Long, name: String)(implicit c: Connection): Album =
    findAlbum(userId, name) getOrElse {
      addAlbum(userId, name)
    }

  def uploadTrack(file: File, originalName: String, uploaderId: Long): Track = db.withConnection { implicit conn =>
    val metadata = analyzer.get().extractMetadata(file)
    val tags = metadata.getMetadata.asScala.map({ case (key, value) => (key.trim.toLowerCase, value) })
    val track = Track(
      title = tags.getOrElse("title", originalName),
      originalName = originalName,
      extension = getExtension(metadata.getPossibleExtensions),
      length = metadata.getLength,
      bitrate = metadata.getBitrate,
      hasCover = metadata.hasCover
    )

    val taggedTrack = (tags.get("album"), tags.get("artist")) match {
      case (None, None) =>
        track
      case (Some(albumName), None) =>
        val album = getAlbumOrCreate(uploaderId, albumName)
        val trackWithAlbum = track.copy(
          albumId = Some(album.id),
          artistId = album.artistId
        )
        trackWithAlbum
      case (None, Some(artistName)) =>
        val artist = getArtistOrCreate(uploaderId, artistName)
        val trackWithArtist = track.copy(
          artistId = Some(artist.id)
        )
        trackWithArtist
      case (Some(albumName), Some(artistName)) =>
        val (albumId, artistId) = findAlbumArtist(uploaderId, albumName, artistName) getOrElse {
          val artist = getArtistOrCreate(uploaderId, artistName)
          val album = addAlbum(uploaderId, albumName, Some(artist.id))
          (album.id, artist.id)
        }
        val trackWithAlbumArtist = track.copy(
          artistId = Some(artistId),
          albumId = Some(albumId)
        )
        trackWithAlbumArtist
    }
    val trackId = addTrack(uploaderId, taggedTrack)
    val insertedTrack = taggedTrack.copy(id = trackId)
    if(insertedTrack.hasCover) insertedTrack.albumId.map(albumId=>addCoverToAlbum(albumId, trackId))
    Files.move(file.toPath, tracksPath.resolve(trackId.toString+"."+track.extension))
    Option(metadata.getCover).foreach(f => Files.move(f.toPath, coversPath.resolve(trackId.toString+".jpg")))
    insertedTrack
  }
}