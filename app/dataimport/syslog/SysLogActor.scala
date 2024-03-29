package dataimport.syslog

import akka.actor.{ ActorRef, Props, Actor }
import dao.SysLogDao
import dataimport.{ CollectStatistics, TriggerSysLogImport }
import parser.SysLogParser
import play.api.Logger
import akka.routing.RoundRobinRouter
import domain.{ CdrVsa, Cdr }
import play.api.db.DB
import akka.util.Timeout
import akka.util.duration._

class SysLogMessages(val statsListener: ActorRef)
case class SysLogMessagesFetch(override val statsListener: ActorRef) extends SysLogMessages(statsListener)
case class SysLogMessagesPersistCdr(cdr: Cdr, override val statsListener: ActorRef) extends SysLogMessages(statsListener)
case class SysLogMessagesPersistCdrVsa(cdrVsa: CdrVsa, override val statsListener: ActorRef) extends SysLogMessages(statsListener)
case class SysLogParse(rawSysLogEntry: String, override val statsListener: ActorRef) extends SysLogMessages(statsListener)

class SysLogImportMaster extends Actor {
  val statsListener = context.actorOf(Props[SysLogImportStatisticsListener], name = "sysLogStatsListener")
  val sysLogMessageFetchWorker = context.actorOf(Props[SysLogMessageFetchWorker], name = "sysLogDao")

  implicit val timeout = Timeout(100 seconds)

  protected def receive = {
    case TriggerSysLogImport => {
      statsListener ! ResetStatistics
      sysLogMessageFetchWorker ! SysLogMessagesFetch(statsListener)
    }

    case CollectStatistics => statsListener.forward(ProvideStatistics)
  }
}

class SysLogParseWorker extends Actor {
  val persistRouter = context.actorOf(Props[SysLogMessagePersistWorker].withRouter(RoundRobinRouter(4)), name = "sysLogPersistRouter")

  protected def receive = {
    case SysLogParse(rawSysLogEntry, statsListener) => {
      SysLogParser.parse(rawSysLogEntry) match {
        case Some(x) => x match {
          case cdr: Cdr => persistRouter ! SysLogMessagesPersistCdr(cdr, statsListener)
          case vsa: CdrVsa => persistRouter ! SysLogMessagesPersistCdrVsa(vsa, statsListener)
        }
        case None =>
      }
    }
  }
}

class SysLogMessagePersistWorker extends Actor {

  import play.api.Play.current

  protected def receive = {
    case message: SysLogMessagesPersistCdrVsa => persistVsa(message.cdrVsa, message.statsListener)
    case message: SysLogMessagesPersistCdr => persistCdr(message.cdr, message.statsListener)
  }

  def persistVsa(vsa: CdrVsa, statsListener: ActorRef) {
    DB.withConnection {
      implicit c =>
        {
          if (SysLogDao.vsaExists(vsa.originalRecord)) {
            statsListener ! DuplicateMessage
          } else {
            SysLogDao.persistCdrVsa(vsa)
            statsListener ! CdrVsaMessage
          }
        }
    }
  }

  def persistCdr(cdr: Cdr, statsListener: ActorRef) {
    DB.withConnection {
      implicit c =>
        {
          if (SysLogDao.cdrExists(cdr.originalRecord)) {
            statsListener ! DuplicateMessage
          } else {
            SysLogDao.persistCdr(cdr)
            statsListener ! CdrMessage
          }
        }
    }
  }
}

class SysLogMessageFetchWorker extends Actor {
  val NrOfParsingActors = 4

  val sysLogParseRouter = context.actorOf(Props[SysLogParseWorker].withRouter(RoundRobinRouter(NrOfParsingActors)), name = "sysLogParseRouter")

  protected def receive = {
    case SysLogMessagesFetch(listener) =>

      Logger.info("Fetching syslog dao")
      findResults(listener)

  }

  def findResults(statsListener: ActorRef): Unit = {
    import play.api.Play.current

    val messageSender = (id: Long, message: String) => {
      sysLogParseRouter ! SysLogParse(message, statsListener)
      statsListener ! RecordFound(1)
    }

    DB.withConnection {
      implicit c =>
        SysLogDao.findAfterId(0, messageSender)
    }
  }
}



