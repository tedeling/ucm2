package dataimport.syslog


import akka.actor.{Props, Actor}
import dao.SysLogDao
import dataimport.SysLogImport
import parser.SysLogParser
import play.api.Logger
import dataimport.ActorUtil._
import akka.routing.RoundRobinRouter
import domain.{AbstractCdr, CdrVsa, Cdr}

sealed trait SysLogMessage

case object SysLogMessagesFetch extends SysLogMessage
case class SysLogMessagesPersistCdr(cdr: Cdr) extends SysLogMessage
case class SysLogMessagesPersistCdrVsa(cdrVsa: CdrVsa) extends SysLogMessage
case class SysLogMessagesResult(sysLogEntries: List[(Long, String)]) extends SysLogMessage
case class SysLogParse(rawSysLogEntry: String) extends SysLogMessage

class SysLogImportWorker extends Actor {
  val NrOfParsingActors = 4

  val sysLogRouter = context.actorOf(Props[SysLogParseWorker].withRouter(RoundRobinRouter(NrOfParsingActors)), name = "sysLogParseRouter")

  protected def receive = {
    case SysLogImport => {
      findOrCreateActor("sysLogDao") {
        new SysLogMessageFetchWorker()
      } ! SysLogMessagesFetch
    }

    case SysLogMessagesResult(value) => {
      Logger.info("Received %d syslog events".format(value.size))

      value map (msg => sysLogRouter ! SysLogParse(msg._2))
    }
  }
}

class SysLogParseWorker extends Actor {
  val persistRouter = context.actorOf(Props[SysLogMessagePersistWorker].withRouter(RoundRobinRouter(4)), name = "sysLogPersistRouter")

  protected def receive = {
    case SysLogParse(entry) => {
      implicit val stats: SysLogParsingStatistics = new SysLogParsingStatistics()

      SysLogParser.parse(entry) match {
        case Some(x) => x match {
          case cdr: Cdr => persistRouter ! SysLogMessagesPersistCdr(cdr)
          case vsa: CdrVsa => persistRouter ! SysLogMessagesPersistCdrVsa(vsa)
        }
        case None =>
      }
    }
  }
}

class SysLogMessagePersistWorker extends Actor {
  protected def receive = {

    case message: SysLogMessagesPersistCdrVsa => {
      Logger.info("persisting vsa")
    }
    case message: SysLogMessagesPersistCdr => {
      SysLogDao.persistCdr(message.cdr)
    }
  }
}

class SysLogMessageFetchWorker extends Actor {
  protected def receive = {
    case SysLogMessagesFetch =>
      Logger.info("Fetching syslog dao")
      val sysLogEntries: List[(Long, String)] = SysLogDao.findAfterId(0)

      sender ! SysLogMessagesResult(sysLogEntries)
  }
}



