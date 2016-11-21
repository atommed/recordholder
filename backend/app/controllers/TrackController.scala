package controllers

import java.io.File
import java.nio.file.{Files, Paths}
import java.sql.Connection
import javax.inject.Inject

import scala.collection.JavaConverters.mapAsScalaMapConverter
import play.api.Configuration
import play.api.db.Database
import play.api.mvc._
import anorm._
import play.api.libs.Files.TemporaryFile
import util.MetadataRetriever

class TrackController @Inject()(db : Database, conf: Configuration) extends Controller {
  val coversPath = Paths.get(conf.getString("storage.covers-path").get)
  val tracksPath = Paths.get(conf.getString("storage.tracks-path").get)
  val analyzerExecutable = Paths.get(conf.getString("ffmpeg-metadata-analyzer.executable").get)
  val analyzer = new ThreadLocal[MetadataRetriever]{
    override def initialValue() = new MetadataRetriever(analyzerExecutable)
  }

  def normalizeMetadata(metadata: Iterable[(String, String)]) : Iterable[(String,String)] = {
    metadata map {case (key,value) => (key.trim.toLowerCase, value.trim)}
  }

  private def saveTrackToDB(length: Double, bitrate: Long)(implicit conn: Connection) : Long = {
    SQL("INSERT INTO track(length, bitrate) values({length},{bitrate})")
      .on("length" -> length, "bitrate" -> bitrate)
      .executeInsert(SqlParser.scalar[Long].single)
  }

  private def saveTrackToFS(id: Long, extension: String, track: TemporaryFile, cover: File) = {
    val idStr = id.toString
    track.moveTo(tracksPath.resolve(idStr+"."+extension).toFile)
    if(cover != null)
      Files.move(cover.toPath, coversPath.resolve(idStr+".jpg"))
  }

  private def saveTrackTags(id: Long, tags: Iterable[(String, String)])(implicit conn: Connection) = {
    val tagParams = (for {
      (key, value) <- tags
    } yield Seq[NamedParameter](
      "key" -> key, "value" -> value
    )).toSeq
    BatchSql(s"INSERT into tag (track_id, key, value) values ($id, {key}, {value})",
      tagParams.head, tagParams.tail:_*).execute()
  }

  def upload = Action(parse.multipartFormData) {
    _.body.file(TrackController.TRACK_FIELD_NAME).map(track=> {
      val result = analyzer.get().extractMetadata(track.ref.file)
      if(result.isExitSuccessful){
        db.withConnection(implicit conn => {
          val metadata = normalizeMetadata(result.getMetadata.asScala)
          val id = saveTrackToDB(result.getLength, result.getBitrate)
          val extension = result.getPossibleExtensions()(0)
          saveTrackToFS(id, extension, track.ref, result.getCover)
          if(metadata.nonEmpty) saveTrackTags(id, metadata)
          Ok("Uploaded!")
        })
      }
      else {
        track.ref.file.delete()
        Option(result.getCover).foreach(_.delete())
        BadRequest("Can't parse uploaded file as track")
      }
    }).getOrElse(
      BadRequest(s"Form must contain file in field ${TrackController.TRACK_FIELD_NAME}")
    )
  }
}

object TrackController{
  val TRACK_FIELD_NAME = "track"
}