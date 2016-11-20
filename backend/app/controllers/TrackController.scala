package controllers

import java.nio.file.{Files, Paths, StandardCopyOption}
import javax.inject.Inject

import scala.collection.JavaConverters.mapAsScalaMapConverter
import play.api.Configuration
import play.api.db.Database
import play.api.mvc._
import anorm._
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

  def upload = Action(parse.multipartFormData) {
    _.body.file(TrackController.TRACK_FIELD_NAME).map(track=> {
      val result = analyzer.get().extractMetadata(track.ref.file)
      if(result.isExitSuccesfull){
        db.withConnection(implicit conn => {
          val id = SQL("INSERT INTO track(length, bitrate) values({length},{bitrate})")
            .on("length" -> result.getLength, "bitrate" -> result.getBitrate)
            .executeInsert(SqlParser.scalar[Long].single)
          val idStr = id.toString
          track.ref.moveTo(tracksPath.resolve(idStr).toFile)
          if(result.getCover != null)
            Files.move(result.getCover.toPath, coversPath.resolve(idStr))
          val metadata = normalizeMetadata(result.getMetadata.asScala)
          Ok(s"Uploaded $idStr<br>${metadata.mkString("<br>")}")
        })
      }
      else BadRequest("Can't parse uploaded file as track")
    }).getOrElse(
      BadRequest(s"Form must contain file in field ${TrackController.TRACK_FIELD_NAME}")
    )
  }
}

object TrackController{
  val TRACK_FIELD_NAME = "track"
}
