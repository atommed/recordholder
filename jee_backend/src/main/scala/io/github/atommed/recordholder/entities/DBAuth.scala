package io.github.atommed.recordholder.entities

import javax.persistence._

import scala.beans.BeanProperty
/**
  * Created by gregory on 13.11.16.
  */

@Entity
@Table
class DBAuth extends Serializable {
  @Id @BeanProperty var login: String = _
  @BeanProperty var passwordHash: Array[Byte] = _
  @BeanProperty var salt: Array[Byte] = _
  @OneToOne @BeanProperty var relatedUser: User = _

  def prettyHash() = passwordHash.map("%02X" format _).mkString
}

object DBAuth {
  def apply(login: String,
            passwordHash: Array[Byte],
            salt: Array[Byte],
            relatedUser: User): DBAuth = {
    val ret = new DBAuth()
    ret.login = login
    ret.passwordHash = passwordHash
    ret.salt = salt
    ret.relatedUser = relatedUser
    ret
  }
}
