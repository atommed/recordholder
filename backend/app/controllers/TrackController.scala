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
import play.api.libs.json.Json
import play.api.libs.Files.TemporaryFile
import util.MetadataRetriever

class TrackController @Inject()(db : Database, conf: Configuration) extends Controller {
  val coversPath = Paths.get(conf.getString("storage.covers-path").get)
  val tracksPath = Paths.get(conf.getString("storage.tracks-path").get)
  val analyzerExecutable = Paths.get(conf.getString("ffmpeg-metadata-analyzer.executable").get)
  val analyzer = new ThreadLocal[MetadataRetriever]{
    override def initialValue() = new MetadataRetriever(analyzerExecutable)
  }

  private def saveTrackToDB(name: String, extension: String)
                           (implicit result: util.MetadataRetriever.Result,
                            conn: Connection) : Long = {
    val length = result.getLength
    val bitrate = result.getBitrate
    val hasCover = result.getCover != null
    SQL"""INSERT INTO track(original_name, extension, length, bitrate, has_cover)
                      VALUES($name, $extension, $length, $bitrate, $hasCover)
       """.executeInsert(SqlParser.scalar[Long].single)
  }

  private def saveTrackToFS(id: Long, extension: String, track: TemporaryFile, cover: File) = {
    val idStr = id.toString
    track.moveTo(tracksPath.resolve(idStr+"."+extension).toFile)
    if(cover != null)
      Files.move(cover.toPath, coversPath.resolve(idStr+".jpg"))
  }

  private def saveTrackTags(id: Long, tags: Iterable[(String, String)])
                           (implicit conn: Connection) = {
    val tagParams = (for {
      (key, value) <- tags
    } yield Seq[NamedParameter](
      "key" -> key, "value" -> value
    )).toSeq
    BatchSql(s"INSERT into tag (track_id, key, value) values ($id, {key}, {value})",
      tagParams.head, tagParams.tail:_*).execute()
  }

  private def getExtension(possibleExtensions: Array[String]): String = {
    val preferableExtensions = "ogg" :: "flac" :: "mp3" :: "m4a" :: Nil
    val matchedPreferable = for{
      extension <- preferableExtensions
      if possibleExtensions.contains(extension)
    } yield extension
    matchedPreferable.headOption match {
      case Some(extension) => extension;
      case None => possibleExtensions(0)
    }
  }

  def upload = Action(parse.multipartFormData) {
    _.body.file(TrackController.TRACK_FIELD_NAME).map(track=> {
      implicit val result = analyzer.get().extractMetadata(track.ref.file)
      if(result.isExitSuccessful){
        db.withConnection(implicit conn => {
          val tags = result.getMetadata.asScala.map({case (key, value)=> (key.trim.toLowerCase, value.trim)})
          val extension = getExtension(result.getPossibleExtensions)
          val id = saveTrackToDB(track.filename, extension)
          saveTrackToFS(id, extension, track.ref, result.getCover)
          if(tags.nonEmpty) saveTrackTags(id, tags)

          val json = Json.obj(
            "id" -> id,
            "extension" -> extension,
            "tags" -> tags
          )
          Ok(json)
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