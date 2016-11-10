package io.github.atommed.recordholder.dao

import io.github.atommed.recordholder.entities.Student

/**
  * Created by gregory on 10.11.16.
  */
trait StudentDAO {
  def findAll : java.util.List[Student]
  def save(s: Student)
}
