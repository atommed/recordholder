package models

case class User(id : Long, name: String)
case class OwnAuth(userId: Long, login: String, passwordHash: Array[Byte], salt: Array[Byte])
case class Artist(id: Long, name: String, description: Option[String])
case class Album(id: Long, artistId: Option[Long], name: String, description: Option[String], coverId: Option[Long])
case class Track(id: Long = -1,
                 title: String = null,
                 originalName: String = null,
                 extension: String = null,
                 length: Double = -1,
                 bitrate: Long = -1,
                 hasCover: Boolean = false,
                 artistId: Option[Long] = None, albumId:Option[Long] = None)
case class Tag(track_id: Long,tag: String, value: String)
case class PlayList(id: Long, name: String)
case class PlayListTrack(id: Long, playlistId: Long, trackId: Long, after: Option[Long])

