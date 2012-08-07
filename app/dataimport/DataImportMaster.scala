package dataimport

import akka.actor.{ActorPath, Props, ActorRef, Actor}
import akka.routing.RoundRobinRouter
import org.joda.time.{LocalDateTime, DateTime}
import syslog.SysLogImportMaster

class DataImportMaster(nrOfWorkers: Int) extends Actor {

  val sysLogRouter = context.actorOf(Props[SysLogImportMaster].withRouter(RoundRobinRouter(nrOfWorkers)), name = "sysLogRouter")

  var status: DataImportStatus = DataImportStatus()

  def receive = {
    case Status => {
      sender ! status
    }
    case TriggerDataImport => {
      status = new DataImportStatus(startTime = Some(new LocalDateTime()), started = true)

      sysLogRouter ! SysLogImport
    }
    case SysLogResult => {
      status = status.copy(endTime = Some(new LocalDateTime()), finished = true)

      println("syslog imported")
    }
  }
}
