package controllers

import scala.collection.convert.wrapAsScala.collectionAsScalaIterable

import java.nio.file.{Files, Paths}
import javax.inject.Inject

import play.api.Configuration
import play.api.mvc._

/**
  * Created by gregory on 20.09.16.
  */

object TrackUploadController{
  private final val TRACK_FIELD_NAME = "track"
}

class TrackUploadController @Inject() (conf:Configuration) extends Controller {
  private val workdir = Files.createTempDirectory("ffmpeg-analyzer-wd")
  private val analyzer = Paths.get(conf.getString("ffmpeg-metadata-analyzer.executable").get)
  private val extractor = new util.MetadataRetriever(workdir, analyzer)

  def handleUpload = Action(parse.multipartFormData){ request =>
    request.body.file(TrackUploadController.TRACK_FIELD_NAME).map(file=>{
      /*val metadata = extractor.extractMetadata(
        file.ref.file,
        "cover.jpg"
      )*/
      val metadata = extractor.extractMetadata(file.ref.file,"cover.jpg")
      val metadata_entries = for(entry <- metadata.getMetadata.entrySet())
        yield entry.getKey + "=" + entry.getValue
      Ok(metadata_entries.mkString("<br>"))
    }
    ).getOrElse(
      BadRequest("Request must contain track with name \""+
        TrackUploadController.TRACK_FIELD_NAME+"\"")
    )
  }
}
