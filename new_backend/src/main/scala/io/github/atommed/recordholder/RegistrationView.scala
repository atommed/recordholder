package io.github.atommed.recordholder

import java.awt.event.ActionEvent
import javax.ejb.EJB
import javax.enterprise.context.SessionScoped
import javax.faces.bean.{ManagedBean, ViewScoped}
import javax.persistence.{EntityManager, PersistenceContext}

import io.github.atommed.recordholder.beans.DBAuthBean
import io.github.atommed.recordholder.entities.DBAuth

import scala.beans.BeanProperty

/**
  * Created by gregory on 13.11.16.
  */

@ManagedBean
@ViewScoped
class RegistrationView extends Serializable {
  @BeanProperty var login: String = ""
  @BeanProperty var password: String = ""
  @EJB var dBAuthBean: DBAuthBean = _

  @PersistenceContext var em: EntityManager = _
  def getAuths = {
    val dbAuth = classOf[DBAuth]
    val q = em.getCriteriaBuilder.createQuery(dbAuth)
    em.createQuery(q.select(q.from(dbAuth))).getResultList
  }

  def logIn() : String = dBAuthBean.authenticate(login,password) match {
    case Some(user) => "good"
    case None => "bad"
  }

  def register() : Unit = {
    dBAuthBean.register(login, password)
  }
}
