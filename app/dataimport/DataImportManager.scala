package dataimport

import akka.actor.{InvalidActorNameException, ActorRef, ActorSystem, Props}
import play.api.libs.concurrent.Akka
import play.api.Play.current
import org.joda.time.DateTime

object DataImportManager {

  val DataImportName = "dataimport"

  def getStatus(): DateTime = {

  }

  def schedule() {
    try {
      val actor: ActorRef = Akka.system.actorOf(Props(new DataImportMaster(1)), name = DataImportName)

      println(actor.path)

      actor ! TriggerDataImport
      } catch {
        case e: InvalidActorNameException => {
         val y = Akka.system.actorFor("/user/" + DataImportName)
          println(y.getClass)
          y ! TriggerDataImport
         println("actor found")
        }
      }
  }

  def getActor() = {
    val ref = actorReference

  }

  def actorReference = Akka.system.actorFor("/user/" + DataImportName)
}
