package controllers

import java.nio.file.Paths
import javax.inject.Inject
import scala.collection.convert.wrapAsScala.collectionAsScalaIterable

import play.api.Configuration
import play.api.mvc._
import util.MetadataRetriever

class TrackController @Inject()(conf: Configuration) extends Controller {
  val analyzerExecutable = Paths.get(conf.getString("ffmpeg-metadata-analyzer.executable").get)
  val analyzer = new ThreadLocal[MetadataRetriever]{
    override def initialValue() = new MetadataRetriever(analyzerExecutable)
  }

  def upload = Action(parse.multipartFormData) {
    _.body.file(TrackController.TRACK_FIELD_NAME).map(track=> {
      val result = analyzer.get().extractMetadata(track.ref.file)
      if(result.isExitSuccesfull){
        val metadata = result.getMetadata.entrySet()
          .map(entry=>s"${entry.getKey}=${entry.getValue}")
          .mkString("<br>")
        Ok(s"${result.getLength} ${result.getBitrate}<br>$metadata")
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
