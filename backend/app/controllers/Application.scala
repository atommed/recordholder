package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.libs.streams.ActorFlow
import play.api.mvc._


class Application @Inject()(implicit system: ActorSystem, materializer: Materializer) extends Controller {
  private var i: Int = 0

  def index = Action {
    Ok("It works!")
  }

  def other(info: String) = TODO

  def sim(num: Long) = Action {
    Ok(s"This is ${42.0f / num}")
  }

  def socket = WebSocket.accept[String, String](req =>
    ActorFlow.actorRef(out => MyWebSocketActor.props(out))
  )
}
