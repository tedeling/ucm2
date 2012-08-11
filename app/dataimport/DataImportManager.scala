package dataimport

import akka.actor.ActorRef
import akka.dispatch.Await
import akka.pattern.ask
import akka.util.Timeout
import akka.util.duration._
import util.ActorUtil
import ActorUtil._

object DataImportManager {
  val DataImportName = "dataimport"

  def status(): DataImportStatus = {
    implicit val timeout = Timeout(5 seconds)
    val future = findOrCreateDataImport ? Status
    Await.result(future, 5 seconds).asInstanceOf[DataImportStatus]
  }

  def schedule() {
    findOrCreateDataImport ! TriggerDataImport
  }

  def findOrCreateDataImport: ActorRef = {
    findOrCreateActor(DataImportName) {
      new DataImportMaster(1)
    }
  }
}
