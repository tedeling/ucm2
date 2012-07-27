package dataimport

import akka.actor.{ActorPath, Props, ActorRef, Actor}
import akka.routing.RoundRobinRouter

class DataImportMaster(nrOfWorkers: Int) extends Actor {
  val sysLogRouter = context.actorOf(Props[SysLogImportWorker].withRouter(RoundRobinRouter(nrOfWorkers)), name = "sysLogRouter")

  def receive = {
    case TriggerDataImport => {
      println("starting")
      sysLogRouter ! SysLogImport
    }
    case SysLogResult => {
      println("syslog imported")
      context.stop(self)
    }
  }
}
