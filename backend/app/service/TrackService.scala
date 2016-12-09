package service

import java.io.File
import java.nio.file.Paths
import javax.inject.{Inject, Singleton}

import models.User
import play.api.Configuration
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import service.components._
import slick.driver.JdbcProfile

import scala.collection.JavaConverters.mapAsScalaMapConverter
import util.MetadataRetriever


@Singleton
class TrackService @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, conf: Configuration)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with TracksComponent
    with UsersComponent
    with UserAlbumsComponent
    with UserArtistsComponent
    with ArtistsComponent
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

  def persistTrack(track: File, uploader: User) = {
    val metadata = analyzer.get().extractMetadata(track)
    val tags = metadata.getMetadata.asScala.map({case (key, value) => (key.trim.toLowerCase, value)})
    val extension = getExtension(metadata.getPossibleExtensions)
    val hasCover = metadata.getCover != null


    

    //userAlbums join albums
    val albumQ = for{
      userAlbum <- userAlbums if userAlbum.userId === uploader.id.bind
      album <- userAlbum.album
      userArtist <- userArtists if userArtist.userId === uploader.id.bind
      artist <- userArtist.artist
    } yield (artist, album)
    albumQ.result.statements.foreach(println)
    db.run(albumQ.result)
  }
}
