package models

case class User(name: String, id: Option[Int] = None)
case class OwnAuth(userId: Option[Int], login: String, passwordHash: Array[Byte], salt:Array[Byte])