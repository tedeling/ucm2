package dataimport

import akka.actor.{InvalidActorNameException, ActorRef, ActorSystem, Props}
import play.api.libs.concurrent.Akka
import play.api.Play.current

object DataImportManager {

  def schedule() {
    try {
        Akka.system.actorOf(Props(new DataImportMaster(1)), name = "dataimport") ! TriggerDataImport
      } catch {
        case e: InvalidActorNameException => {
         val y = Akka.system.actorFor("dataimport")
          println(y.getClass)
          y ! TriggerDataImport
         println("actor found")
        }
      }
  }
}
