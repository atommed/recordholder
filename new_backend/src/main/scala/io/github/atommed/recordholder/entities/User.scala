package io.github.atommed.recordholder.entities

import javax.persistence._

import scala.beans.{BeanProperty, BooleanBeanProperty}

/**
  * Created by gregory on 11.11.16.
  */

@Entity
@Table(name = "app_user")
class User extends Serializable {
  def this(name: String) = {
    this()
    this.name = name
  }

  @Id @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty var id: Long = _
  @BeanProperty var name: String = _
  @BooleanBeanProperty var moderator = false
}