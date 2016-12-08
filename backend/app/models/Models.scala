package models

case class User(id: Option[Long] = None, name: String)
case class OwnAuth(userId: Option[Long], login: String, passwordHash: Array[Byte], salt: Array[Byte])
case class Artist(id: Option[Long], name: String, description: String)
case class Album(id: Option[Long], artistId: Option[Long], name: String, description: String)
case class Track(id: Option[Long],
                 title: String,
                 originalName: String,
                 extension: String,
                 length: Float,
                 bitrate: Int,
                 hasCover: Boolean,
                 artistId: Option[Long], albumId:Option[Long])
case class Tag(track_id: Option[Long],tag: String, value: String)
case class PlayList(id: Option[Long], name: String)
case class PlayListTrack(id: Option[Long], playlistId: Option[Long], trackId: Option[Long], after: Option[Long])