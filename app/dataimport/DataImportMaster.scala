package dataimport

import akka.actor.{Props, Actor}
import akka.routing.RoundRobinRouter
import syslog.{ProvideStatistics, ResetStatistics, DataImportStatisticsListener, SysLogImportMaster}

class DataImportMaster(nrOfWorkers: Int) extends Actor {

  val sysLogRouter = context.actorOf(Props[SysLogImportMaster].withRouter(RoundRobinRouter(nrOfWorkers)), name = "sysLogRouter")
  val statsListener = context.actorOf(Props[DataImportStatisticsListener], name = "statisticsListener")

  def receive = {
    case Status => statsListener.forward(ProvideStatistics)

    case TriggerDataImport => {
      statsListener ! ResetStatistics
      sysLogRouter ! SysLogImport(statsListener)
    }
  }
}
