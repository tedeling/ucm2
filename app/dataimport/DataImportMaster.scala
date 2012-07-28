package dataimport

import akka.actor.{ActorPath, Props, ActorRef, Actor}
import akka.routing.RoundRobinRouter
import org.joda.time.{LocalDateTime, DateTime}

class DataImportMaster(nrOfWorkers: Int) extends Actor {
  val sysLogRouter = context.actorOf(Props[SysLogImportWorker].withRouter(RoundRobinRouter(nrOfWorkers)), name = "sysLogRouter")

  var status: DataImportStatus = _

  def receive = {
    case Status => {
      sender ! status
    }
    case TriggerDataImport => {
      status = new DataImportStatus(startTime = new LocalDateTime())

      sysLogRouter ! SysLogImport
    }
    case SysLogResult => {
      status = status.copy(endTime = new LocalDateTime(), finished = true)

      println("syslog imported")
    }
  }
}
