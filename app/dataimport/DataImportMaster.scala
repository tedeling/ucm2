package dataimport

import akka.actor.{Props, ActorRef, Actor}
import akka.routing.RoundRobinRouter

class DataImportMaster(nrOfWorkers: Int) extends Actor {
  val sysLogRouter = context.actorOf(Props[SysLogImportWorker].withRouter(RoundRobinRouter(nrOfWorkers)), name = "sysLogRouter")

  def receive = {
    case TriggerDataImport => sysLogRouter ! SysLogImport
    case SysLogResult => {
      println("syslog imported")
      context.stop(self)
    }
  }
}
