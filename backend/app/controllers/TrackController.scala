package controllers

import java.io.File
import java.nio.file.{Files, Paths}
import java.sql.Connection
import javax.inject.Inject

import models.User
import play.api.Configuration
import play.api.libs.Files.TemporaryFile
import play.api.mvc._
import service.TrackService
import util.MetadataRetriever

class TrackController @Inject()(trackService: TrackService, conf: Configuration) extends Controller {
  def upload = Action(parse.multipartFormData) {
    _.body.file(TrackController.TRACK_FIELD_NAME).map(track => {
      trackService.persistTrack(track.ref.file,track.filename, User(0, "kek"))
      Ok("good start")
    }).getOrElse(Ok("bad start"))
  }
}

object TrackController {
  val TRACK_FIELD_NAME = "track"
}