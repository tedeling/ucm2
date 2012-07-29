package dataimport.syslog


import akka.actor.Actor
import dao.SysLogDao
import dataimport.SysLogImport
import play.api.Logger
import dataimport.ActorUtil._

sealed trait SysLogMessage
case object SysLogMessagesFetch extends SysLogMessage
case class SysLogMessagesResult(sysLogEntries: List[(Long, String)]) extends SysLogMessage


class SysLogImportWorker extends Actor {

  protected def receive = {
    case SysLogImport => {
      findOrCreateActor("sysLogDao") {
        new SysLogDaoWorker()
      } ! SysLogMessagesFetch
    }

    case SysLogMessagesResult(value) => {
      Logger.info("Received %d syslog events".format(value.size))

    }
  }

  class SysLogDaoWorker extends Actor {
    protected def receive = {
      case SysLogMessagesFetch =>
        Logger.info("Fetching syslog dao")
        val sysLogEntries: List[(Long, String)] = SysLogDao.findAfterId(0)

        sender ! SysLogMessagesResult(sysLogEntries)
    }
  }

}
