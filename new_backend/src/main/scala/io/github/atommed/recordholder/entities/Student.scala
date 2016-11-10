package io.github.atommed.recordholder.entities

import javax.persistence._

import scala.beans.BeanProperty

/**
  * Created by gregory on 10.11.16.
  */

@Entity
@Table
class Student(@BeanProperty var name: String) {
  def this() = this(null)

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  @BeanProperty
  var id : Int = _;
}



