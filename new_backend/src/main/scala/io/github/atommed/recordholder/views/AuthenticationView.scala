package io.github.atommed.recordholder.views

import javax.ejb.EJB
import javax.faces.bean.{ManagedBean, SessionScoped}
import javax.persistence.{EntityManager, PersistenceContext}

import io.github.atommed.recordholder.beans.DBAuthBean
import io.github.atommed.recordholder.entities.DBAuth

import scala.beans.BeanProperty

/**
  * Created by gregory on 13.11.16.
  */

@ManagedBean
@SessionScoped
class AuthenticationView extends Serializable {

  @BeanProperty var login: String = _
  @BeanProperty var password: String = _

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
