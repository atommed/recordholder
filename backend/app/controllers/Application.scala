package controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.i18n.{I18nSupport, MessagesApi}

case class CSRFData(name: String)

class Application @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport{

  val csrfForm = Form(
    mapping("name" -> text)(CSRFData.apply)(CSRFData.unapply)
  )

  def index = Action {
    Ok(views.html.CSRF.index(csrfForm))
  }

  def csrf = Action {
    Ok("Hackd")
  }
}
