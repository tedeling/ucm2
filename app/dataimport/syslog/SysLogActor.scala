package dataimport.syslog


import akka.actor.{Props, Actor}
import dao.SysLogDao
import dataimport.SysLogImport
import parser.SysLogParser
import play.api.Logger
import dataimport.ActorUtil._
import akka.routing.RoundRobinRouter
import domain.{CdrVsa, Cdr}
import play.api.db.DB

sealed trait SysLogMessage

case object SysLogMessagesFetch extends SysLogMessage

case class SysLogMessagesPersistCdr(cdr: Cdr) extends SysLogMessage

case class SysLogMessagesPersistCdrVsa(cdrVsa: CdrVsa) extends SysLogMessage

case class SysLogMessagesResult(sysLogEntries: List[(Long, String)]) extends SysLogMessage

case class SysLogParse(rawSysLogEntry: String) extends SysLogMessage

class SysLogImportMaster extends Actor {
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
  import play.api.Play.current

  protected def receive = {
    case message: SysLogMessagesPersistCdrVsa => persistVsa(message.cdrVsa)
    case message: SysLogMessagesPersistCdr =>  persistCdr(message.cdr)
  }

  def persistVsa(vsa: CdrVsa) {
    DB.withConnection {
      implicit c => {
        if (SysLogDao.vsaExists(vsa.originalRecord)) {
          Logger.info("vsa exists")
        } else {
          SysLogDao.persistCdrVsa(vsa)
        }
      }
    }
  }

  def persistCdr(cdr: Cdr) {
    DB.withConnection {
      implicit c => {
        if (SysLogDao.cdrExists(cdr.originalRecord)) {
          Logger.info("cdr exists")
        } else {
          SysLogDao.persistCdr(cdr)
        }
      }
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



