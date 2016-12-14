package service

import java.io.File
import java.nio.file.Paths
import javax.inject.{Inject, Singleton}

import models.{Album, User}
import play.api.Configuration
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import service.components._
import slick.driver.JdbcProfile

import scala.collection.JavaConverters.mapAsScalaMapConverter
import util.MetadataRetriever

import scala.concurrent.Future


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


  private def existingAlbum1Q(uploaderId: Rep[Long], albumName: Rep[String]) = {
    userAlbums join albums on (_.albumId === _.id) joinLeft artists on {
      case ((user, album), artist) => album.artistId === artist.id
    } filter {
      case ((user, album), artist) => user.userId === uploaderId && album.name === albumName
    } map {
      case ((user, album), artist) => (album.id, artist map(_.id))
    }
  }


  private def existingAlbumQ(uploader: Rep[Long], albumName: Rep[String], artistName: Rep[String]) = {
    userAlbums join albums on (_.albumId === _.id) joinLeft artists on {
      case ((_, album), artist) => album.artistId === artist.id
    } filter {case ((user,album), _) => album.name === albumName && user.userId === uploader } sortBy {
      case ((_,_), artist) =>
        ((artist.isEmpty || artist.map(_.name === artistName)).desc, artist.isEmpty)
    } map {case ((_,album), artist) => (album.name, artist.map(_.name))  }
  }

  private val existingAlbumC = Compiled(existingAlbum1Q _)

  def persistTrack(track: File, uploader: User) = {
    val metadata = analyzer.get().extractMetadata(track)
    val tags = metadata.getMetadata.asScala.map({case (key, value) => (key.trim.toLowerCase, value)})
    val extension = getExtension(metadata.getPossibleExtensions)
    val hasCover = metadata.getCover != null

    albums.filter(_.artistId.isDefined)

    existingAlbumQ(1l.bind, "al", "ar1n").result.statements.foreach(println)

    val q1 = tags.get("album") match {
      case Some(tagAlbum) =>
        Future.successful(None)
      case None => Future.successful(None)
    }

  }
}
