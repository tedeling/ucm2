package dataimport

import acd.{TriggerAcdImport, AcdImportMaster}
import akka.actor.{Props, Actor}
import akka.routing.RoundRobinRouter
import syslog.{ProvideStatistics, ResetStatistics, SysLogImportStatisticsListener, SysLogImportMaster}

class DataImportMaster(nrOfWorkers: Int) extends Actor {

  val sysLogMaster = context.actorOf(Props[SysLogImportMaster], name = "sysLogActor")
  val acdRouter = context.actorOf(Props[AcdImportMaster].withRouter(RoundRobinRouter(nrOfWorkers)), name = "acdRouter")

  def receive = {
    case CollectStatistics => sysLogMaster.forward(CollectStatistics)

    case TriggerSysLogImport => {
      sysLogMaster ! TriggerSysLogImport
      acdRouter ! TriggerAcdImport
    }
  }
}
