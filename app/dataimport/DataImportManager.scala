package dataimport

import akka.actor.ActorRef
import akka.dispatch.Await
import akka.pattern.ask
import akka.util.Timeout
import akka.util.duration._
import syslog.SysLogImportStatistics
import util.ActorUtil
import ActorUtil._

object DataImportManager {
  val DataImportName = "dataimport"

  def status(): Option[SysLogImportStatistics] = {
    implicit val timeout = Timeout(2 seconds)
    val future = findOrCreateDataImport ? Status
    Await.result(future, 2 seconds).asInstanceOf[Option[SysLogImportStatistics]]
  }

  def schedule():Boolean = {
    if (status() match {
      case Some(status) => status.finished
      case None => true
    }) {
      findOrCreateDataImport ! TriggerSysLogImport
      true
    } else {
      false
    }
  }

  def findOrCreateDataImport: ActorRef = {
    findOrCreateActor(DataImportName) {
      new DataImportMaster(1)
    }
  }
}
