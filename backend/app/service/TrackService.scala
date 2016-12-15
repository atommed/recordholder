package service

import java.io.File
import java.nio.file.{Files, Paths}
import javax.inject.{Inject, Singleton}

import models.{Album, Track, User}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Configuration
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import service.components._
import slick.driver.JdbcProfile

import scala.collection.JavaConverters.mapAsScalaMapConverter
import util.MetadataRetriever

import scala.util.{Failure, Success}


@Singleton
class TrackService @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, conf: Configuration)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with TracksComponent
    with UsersComponent
    with UserAlbumsComponent
    with UserArtistsComponent
    with ArtistsComponent
    with AlbumsComponent
{
  import driver.api._

  private val storage = Paths.get(conf.getString("storage").get)
  private val tracksPath = storage.resolve("tracks")
  private val coversPath = storage.resolve("covers")
  private val analyzerExecutable = Paths.get(conf.getString("ffmpeg-metadata-analyzer.executable").get)
  private val analyzer = new ThreadLocal[MetadataRetriever]{
    override def initialValue(): MetadataRetriever = new MetadataRetriever(analyzerExecutable)
  }

  private val tracks = TableQuery[Tracks]
  private val users = TableQuery[Users]
  private val userArtists = TableQuery[UserArtists]
  private val userAlbums = TableQuery[UserAlbums]
  val albums = TableQuery[Albums]
  val artists = TableQuery[Artists]

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

  def findExistingAlbQ(userId: Rep[Long], albumName: Rep[String]) = {
    for{
      ua <- userAlbums if ua.userId === userId
      album <- albums if album.name === albumName
      (album, artist) <- albums.filter(_.name === albumName).joinLeft(artists).on(_.artistId === _.id)
    } yield (album.id, artist.map(_.id))
  }

  def f1indExistingAlbQ(userId: Rep[Long], albumName: Rep[String]) ={
    userAlbums
      .filter(_.userId === userId)
      .join(albums).on(_.albumId === _.id)
      .filter({case (_, album) => album.name === albumName})
      .joinLeft(artists).on({case ((_, album), artist) => album.artistId === artist.id})
      .sortBy({case (_, artist) => artist.isEmpty.desc})
      .map({case ((_, album), artist) => (album.id, artist.map(_.id))})
  }

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
}
