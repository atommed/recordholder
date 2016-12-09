package models

case class User(id : Long = -1, name: String)
case class OwnAuth(userId: Long, login: String, passwordHash: Array[Byte], salt: Array[Byte])
case class Artist(id: Long, name: String, description: Option[String])
case class Album(id: Long, artistId: Option[Long], name: String, description: Option[String])
case class Track(id: Long,
                 title: String,
                 originalName: String,
                 extension: String,
                 length: Float,
                 bitrate: Int,
                 hasCover: Boolean,
                 artistId: Option[Long], albumId:Option[Long])
case class Tag(track_id: Long,tag: String, value: String)
case class PlayList(id: Long, name: String)
case class PlayListTrack(id: Long, playlistId: Long, trackId: Long, after: Option[Long])