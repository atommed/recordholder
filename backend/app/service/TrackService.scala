package service

import java.io.File
import java.nio.file.{Files, Paths}
import javax.inject.{Inject, Singleton}

import models.{Album, Track, User}
import play.api.Configuration

import scala.collection.JavaConverters.mapAsScalaMapConverter
import util.MetadataRetriever

import scala.util.{Failure, Success}


@Singleton
class TrackService @Inject()(conf: Configuration) {
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

  def persistTrack(track: File, originalName: String, uploader: User) = {
    val metadata = analyzer.get().extractMetadata(track)
    val tags = metadata.getMetadata.asScala.map({case (key, value) => (key.trim.toLowerCase, value)})
    val extension = getExtension(metadata.getPossibleExtensions)

    (tags.get("album"), tags.get("artist")) match {
      case (None, None) =>
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
