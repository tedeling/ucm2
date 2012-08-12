package dataimport.syslog


import akka.actor.{ActorRef, Props, Actor}
import dao.SysLogDao
import dataimport.SysLogImport
import parser.SysLogParser
import play.api.Logger
import akka.routing.RoundRobinRouter
import domain.{CdrVsa, Cdr}
import play.api.db.DB
import akka.util.Timeout
import akka.util.duration._
import akka.pattern.ask
import akka.dispatch.Await

class SysLogMessages(val statsListener: ActorRef)

case class SysLogMessagesFetch(override val statsListener: ActorRef) extends SysLogMessages(statsListener)
case class SysLogMessagesPersistCdr(cdr: Cdr, override val statsListener: ActorRef) extends SysLogMessages(statsListener)
case class SysLogMessagesPersistCdrVsa(cdrVsa: CdrVsa, override val statsListener: ActorRef) extends SysLogMessages(statsListener)
case class SysLogMessagesResult(sysLogEntries: List[(Long, String)], override val statsListener: ActorRef) extends SysLogMessages(statsListener)
case class SysLogParse(rawSysLogEntry: String, override val statsListener: ActorRef) extends SysLogMessages(statsListener)

class SysLogImportMaster extends Actor {
  val NrOfParsingActors = 4

  val sysLogParseWorker = context.actorOf(Props[SysLogParseWorker].withRouter(RoundRobinRouter(NrOfParsingActors)), name = "sysLogParseRouter")
  val sysLogMessageFetchWorker = context.actorOf(Props[SysLogMessageFetchWorker], name = "sysLogDao")

  implicit val timeout = Timeout(100 seconds)

  protected def receive = {
    case SysLogMessagesResult(sysLogEntries, statsListener) => {
      val size = sysLogEntries.size
      statsListener ! SessionSize(size)
      Logger.info("Received %d syslog events".format(size))

      sysLogEntries map (msg => sysLogParseWorker ! SysLogParse(msg._2, statsListener))
    }

    case SysLogImport(statsListener) => {
      sysLogMessageFetchWorker ! SysLogMessagesFetch(statsListener)
//      val fetchFuture = sysLogMessageFetchWorker ? SysLogMessagesFetch mapTo manifest[List[(Long, String)]]
//      fetchFuture map (_ map (msg => sysLogParseWorker ! SysLogParse(msg._2, statsListener)))
//      Await.result(fetchFuture, 100 seconds)
    }
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
      implicit c => {
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
      implicit c => {
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
  protected def receive = {
    case SysLogMessagesFetch(listener) =>
      Logger.info("Fetching syslog dao")
      sender ! SysLogMessagesResult(SysLogDao.findAfterId(0), listener)
  }
}



