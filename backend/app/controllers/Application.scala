package controllers

import play.api.mvc._

class Application extends Controller {
  def index = Action {
    Ok("It works!")
  }

  def other(info: String) = TODO

  def sim(num: Long) = Action {
    Ok(s"This is ${42.0f / num}")
  }
}
