package dataimport

import akka.actor.{ActorRef, InvalidActorNameException, Props}
import play.api.libs.concurrent.Akka
import play.api.Play.current
import akka.dispatch.Await
import akka.pattern.ask
import akka.util.Timeout
import akka.util.duration._

object DataImportManager {

  val DataImportName = "dataimport"

  def status(): DataImportStatus = {
    implicit val timeout = Timeout(5 seconds)
    val future = findDataImportMaster() ? Status
    Await.result(future, 1 second).asInstanceOf[DataImportStatus]
  }

  def schedule() {
    findDataImportMaster() ! TriggerDataImport
  }

  def findDataImportMaster(): ActorRef = {
    try {
      Akka.system.actorOf(Props(new DataImportMaster(1)), name = DataImportName)
    } catch {
      case e: InvalidActorNameException => actorReference
    }
  }

  def actorReference = Akka.system.actorFor("/user/" + DataImportName)
}
