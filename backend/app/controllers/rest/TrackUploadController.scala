package controllers.rest

import javax.inject.Inject

import models.Track
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc._
import service.TrackUploadService
import util.WebAuth.getUserId

import scala.util.{Failure, Success}

class TrackUploadController @Inject()(trackService: TrackUploadService, conf: Configuration) extends Controller {

  def upload = Action(parse.multipartFormData) {implicit req=>
    val res = for {
      userId <- getUserId
      trackFile <- req.body.file(TrackUploadController.TRACK_FIELD_NAME)
      track <- trackService.uploadTrack(trackFile.ref.file, trackFile.filename, userId)
    } yield Ok(Json.toJson(track)(Json.writes[Track]))
    res.getOrElse(BadRequest("Can't parse request"))
  }
}

object TrackUploadController {
  val TRACK_FIELD_NAME = "track"
}