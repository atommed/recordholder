package service

import java.io.File
import java.nio.file.Paths
import java.sql.Connection
import javax.inject.{Inject, Singleton}

import anorm.SqlParser.scalar
import anorm._
import models.{Album, Artist, Track, User}
import play.api.Configuration
import play.api.db.Database
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import util.DBUtils.Implicit.FuturedDb
import util.MetadataRetriever

import scala.collection.JavaConverters.mapAsScalaMapConverter

@Singleton
class TrackService @Inject()(db: Database, conf: Configuration) {
  private val storage = Paths.get(conf.getString("storage").get)
  private val tracksPath = storage.resolve("tracks")
  private val coversPath = storage.resolve("covers")
  private val analyzerExecutable = Paths.get(conf.getString("ffmpeg-metadata-analyzer.executable").get)
  private val analyzer = new ThreadLocal[MetadataRetriever]{
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

  def addTrack(u: User, t: Track)(implicit c: Connection): Long = {
    val newTrackId =
      SQL"""
          INSERT INTO track
          (title, original_name, extension, length, bitrate, has_cover, artist_id, album_id)
          VALUES
          (${t.title}, ${t.originalName}, ${t.extension}, ${t.length}, ${t.bitrate}, ${t.hasCover}, ${t.artistId}, ${t.albumId})
       """.executeInsert(scalar[Long].single)
    SQL"INSERT INTO user_tracks(user_id, track_id) VALUES(${u.id}, ${t.id})".execute()
    newTrackId
  }

  def findAlbum(u: User, name: String)(implicit c: Connection) = {
    val parser: RowParser[Album] = Macro.namedParser[Album]
    SQL"""
         SELECT id, artist_id, name, description
         FROM user_albums JOIN album ON user_albums.user_id = album.id
         WHERE user_albums.user_id = ${u.id} AND album.name = $name
         ORDER BY album.artist_id NULLS FIRST
         LIMIT 1
       """.as(parser.singleOpt)
  }

  def findArtist(u: User, name: String)(implicit c: Connection) = {
    val parser: RowParser[Artist] = Macro.namedParser[Artist]
    SQL"""
         SELECT id, name, description
         FROM user_artists JOIN artist ON user_artists.user_id = artist.id
         WHERE user_artists.user_id = ${u.id} AND artist.name = $name
         LIMIT 1
       """.as(parser.singleOpt)
  }

  def addArtist(u: User, name: String)(implicit c: Connection) = {
    val artistId=
      SQL"""
            INSERT INTO artist(name) VALUES($name)
         """
      .executeInsert(scalar[Long].single)
    SQL"""
         INSERT INTO user_artists(user_id, artist_id) VALUES (${u.id}, $artistId)
       """.executeInsert()
    artistId
  }

  def addAlbum(u: User, name: String)(implicit c: Connection) = {
    val albumId =
      SQL"""
           INSERT INTO album(name) VALUES($name)
         """
        .executeInsert(scalar[Long].single)
    SQL"""
         INSERT INTO user_albums(user_id, album_id) VALUES(${u.id}, $albumId)
       """
      .executeInsert()
    albumId
  }


  def persistTrack(file: File, originalName: String, uploader: User) = {
    val metadata = analyzer.get().extractMetadata(file)
    val tags = metadata.getMetadata.asScala.map({case (key, value) => (key.trim.toLowerCase, value)})
    val extension = getExtension(metadata.getPossibleExtensions)
    val title = tags.get("title")
    val length = metadata.getLength
    val bitrate = metadata.getBitrate
    val hasCover = metadata.hasCover

    val track = Track(
      title = tags.getOrElse("title", originalName),
      originalName = originalName,
      extension = getExtension(metadata.getPossibleExtensions),
      length = metadata.getLength,
      bitrate = metadata.getBitrate,
      hasCover = metadata.hasCover
    )

    val q = (tags.get("album"), tags.get("artist")) match {
      case (None, None) =>
        db.futureQuery(addTrack(uploader,track)(_))

      case (Some(album), None) =>
        db.futureQuery(findAlbum(uploader,album)(_)).flatMap({
          case Some(album)=>
            db.futureQuery(addTrack(uploader,track.copy(albumId = Some(album.id), artistId = album.artistId))(_))
          case None =>
            for{
              albumId <- db.futureQuery(addAlbum(uploader, album)(_))
              trackId <- db.futureQuery(addTrack(uploader, track)(_))
            } yield trackId
        })

      case (None, Some(artist)) =>
        db.futureQuery(findArtist(uploader, artist)(_)).flatMap({
          case Some(artist)=>
            db.futureQuery(addTrack(uploader, track.copy(artistId = Some(artist.id)))(_))
          case None=>
            for{
              artistId <- db.futureQuery(addAlbum(uploader, artist)(_))
              trackId <- db.futureQuery(addTrack(uploader, track.copy(artistId = Some(artistId)))(_))
            } yield trackId
        })

      case _ => scala.concurrent.Future.successful(42l)
    }
  }
  /*

  def persistTrack(track: File, originalName: String, uploader: User) = {
    val metadata = analyzer.get().extractMetadata(track)
    val tags = metadata.getMetadata.asScala.map({case (key, value) => (key.trim.toLowerCase, value)})
    val extension = getExtension(metadata.getPossibleExtensions)
    val hasCover = metadata.getCover != null

    val newTrack = Track(
      title = tags.getOrElse("title", originalName),
      originalName = originalName,
      length = metadata.getLength,
      extension = getExtension(metadata.getPossibleExtensions),
      bitrate = metadata.getBitrate,
      hasCover = metadata.hasCover
    )

    (tags.get("album"), tags.get("1artist")) match {
      case (None, None) =>
        db.run(tracks returning tracks.map(_.id) += newTrack).andThen({
          case Success(id) =>
            Files.move(track.toPath, tracksPath.resolve(id.toString + "." + extension))
            Option(metadata.getCover).map(cover=>Files.move(cover.toPath, coversPath.resolve(id.toString)))
          case Failure(err) =>
            track.delete()
            Option(metadata.getCover).map(_.delete)
        })

      case (Some(album), None) =>
        findExistingAlbQ(uploader.id.bind, album.bind).result.statements.foreach(println)
      case _=>
    }
  }
  */
}
