package controllers

import javax.inject.Inject
import play.api.mvc._


class Application @Inject()() extends Controller {
  def index = Action(Ok("It works!"))
}
