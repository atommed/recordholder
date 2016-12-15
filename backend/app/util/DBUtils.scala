package util

import java.sql.Connection

import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
  * Created by gregory on 15.12.16.
  */
object DBUtils {
  object Implicit {
    implicit class FuturedDb(db: Database)(implicit ec: ExecutionContext) {
      def futureTransaction[A](block: (Connection) => A): Future[A] = Future {
        db.withTransaction(block)
      }

      def futureQuery[A](block: (Connection) => A): Future[A] = Future {
        db.withConnection(block)
      }
    }
  }
}