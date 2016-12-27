package controllers.rest

import javax.inject.{Inject, Singleton}

import models.{Album, Artist, Track}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import service.TrackCollectionService

/**
  * Created by grego on 27.12.2016.
  */

@Singleton
class CollectionController @Inject()(private val collection: TrackCollectionService) extends Controller{
  def albums(userId: Long) = Action {
    implicit val writes = Json.writes[Album]
    val albums = collection.findUserAlbums(userId)
    Ok(Json.toJson(albums))
  }

  def artists(userId: Long) = Action {
    implicit val writes = Json.writes[Artist]
    val artists = collection.findUserArtists(userId)
    Ok(Json.toJson(artists))
  }

  def albumTracks(albumId: Long) = Action{
    implicit val writes = Json.writes[Track]
    val tracks = collection.findAlbumTracks(albumId)
    Ok(Json.toJson(tracks))
  }
}
