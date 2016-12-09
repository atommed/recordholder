package service.components

import models.Artist
import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

/**
  * Created by gregory on 09.12.16.
  */
trait ArtistsComponent {
  this: HasDatabaseConfigProvider[JdbcProfile] =>
  import driver.api._

  class Artists(tag: Tag) extends Table[Artist](tag, "artists"){
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def description = column[Option[String]]("description")

    def * = (id, name, description) <> (Artist.tupled, Artist.unapply)
  }
}
