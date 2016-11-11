package io.github.atommed.facesl

import javax.annotation.Resource
import javax.ejb.EJB
import javax.faces.application.FacesMessage
import javax.faces.bean.ManagedBean
import javax.faces.context.FacesContext
import javax.persistence.{EntityManager, PersistenceContext}
import javax.transaction.UserTransaction

import io.github.atommed.recordholder.dao.StudentDAO
import io.github.atommed.recordholder.entities.Student

import scala.util.Random

@ManagedBean
class StudentsView {
	val rnd = new Random()
  @EJB
  var studentDao : StudentDAO = _

  def getStudents = studentDao.findAll

	def execute : Unit = {
    val newStudent = new Student(rnd.nextString(5))
    studentDao.save(newStudent)
    FacesContext.getCurrentInstance().addMessage(
        null, 
        new FacesMessage(FacesMessage.SEVERITY_INFO,newStudent.id.toString, newStudent.name)
    )
		
	}
}
