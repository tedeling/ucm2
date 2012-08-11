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

case object SysLogMessagesFetch

case class SysLogMessagesPersistCdr(cdr: Cdr, override val statsListener: ActorRef) extends SysLogMessages(statsListener)

case class SysLogMessagesPersistCdrVsa(cdrVsa: CdrVsa, override val statsListener: ActorRef) extends SysLogMessages(statsListener)

case class SysLogMessagesResult(sysLogEntries: List[(Long, String)], override val statsListener: ActorRef) extends SysLogMessages(statsListener)

case class SysLogParse(rawSysLogEntry: String, override val statsListener: ActorRef) extends SysLogMessages(statsListener)

class SysLogImportMaster extends Actor {
  val NrOfParsingActors = 4

  val sysLogParseWorker = context.actorOf(Props[SysLogParseWorker].withRouter(RoundRobinRouter(NrOfParsingActors)), name = "sysLogParseRouter")
  val sysLogMessageFetchWorker = context.actorOf(Props[SysLogMessageFetchWorker], name = "sysLogDao")

  implicit val timeout = Timeout(5 seconds)

  protected def receive = {

    case SysLogImport(statsListener) => {
      val fetchFuture = sysLogMessageFetchWorker ? SysLogMessagesFetch mapTo manifest[List[(Long, String)]]
      fetchFuture map (_ map (msg => sysLogParseWorker ! SysLogParse(msg._2, statsListener)))
      Await.result(fetchFuture, 10 seconds)
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
          Logger.info("vsa exists")
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
          Logger.info("cdr exists")
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
    case SysLogMessagesFetch =>
      Logger.info("Fetching syslog dao")
      SysLogDao.findAfterId(0)
  }
}



