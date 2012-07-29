package dataimport

import akka.actor.{Actor, InvalidActorNameException, Props, ActorRef}
import play.api.libs.concurrent.Akka
import play.api.Play.current

object ActorUtil {
  def findOrCreateActor(actorName: String)(block: => Actor): ActorRef = {
    try {
      Akka.system.actorOf(Props(block), name = actorName)
    } catch {
      case e: InvalidActorNameException => actorReference(actorName)
    }
  }

  def actorReference(name: String) = Akka.system.actorFor("/user/%s".format(name))
}
