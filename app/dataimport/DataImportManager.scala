package dataimport

import akka.actor.ActorRef
import akka.dispatch.Await
import akka.pattern.ask
import akka.util.Timeout
import akka.util.duration._
import syslog.DataImportStatistics
import util.ActorUtil
import ActorUtil._

object DataImportManager {
  val DataImportName = "dataimport"

  def status(): DataImportStatistics = {
    implicit val timeout = Timeout(2 seconds)
    val future = findOrCreateDataImport ? Status
    Await.result(future, 2 seconds).asInstanceOf[DataImportStatistics]
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
