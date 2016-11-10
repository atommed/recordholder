package io.github.atommed.recordholder.beans

import javax.ejb.Stateless
import javax.persistence.{EntityManager, PersistenceContext}

import io.github.atommed.recordholder.dao.StudentDAO
import io.github.atommed.recordholder.entities.Student

/**
  * Created by gregory on 10.11.16.
  */

@Stateless
class StudentBean extends StudentDAO{
  @PersistenceContext
  private var em : EntityManager = _

  def findAll : java.util.List[Student] = {
    val student = classOf[Student]
    val query = em.getCriteriaBuilder.createQuery(student)
    em.createQuery(query.select(query.from(student))).getResultList
  }

  def save(s: Student) = {
    em.persist(s)
  }
}
