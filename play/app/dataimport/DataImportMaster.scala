package dataimport

import akka.actor.{ActorPath, Props, ActorRef, Actor}
import akka.routing.RoundRobinRouter
import syslog.SysLogImportMaster

class DataImportMaster(nrOfWorkers: Int) extends Actor {
  val sysLogRouter = context.actorOf(Props[SysLogImportMaster].withRouter(RoundRobinRouter(nrOfWorkers)), name = "sysLogRouter")

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
