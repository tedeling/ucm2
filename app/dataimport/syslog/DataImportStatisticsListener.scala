package dataimport.syslog

import akka.actor.Actor
import org.joda.time.LocalDateTime

class DataImportStatistics {
  private var cdr: Int = 0
  private var vsa: Int = 0
  private var dupes: Int = 0
  private var startTime: LocalDateTime = new LocalDateTime()
  private var endTime: Option[LocalDateTime] = None
  private[syslog] var size: Int = _

  def cdrCount = cdr
  def vsaCount = vsa
  def dupeCount = dupes
  def start = startTime
  def end = endTime

  def addDupe() {
    dupes = dupes + 1
    determineState()
  }

  def addCdr() {
    cdr = cdr + 1
    determineState()
  }

  def addVsa() {
    vsa = vsa + 1
    determineState()
  }

  private def determineState() {
    if (dupes + cdr + vsa == dupes) {
      endTime = Some(new LocalDateTime())
    }
  }

  def finished: Boolean = endTime.isDefined
}

class DataImportStatisticsListener extends Actor {
  var stats = new DataImportStatistics

  override def receive = {
    case ResetStatistics => stats = new DataImportStatistics
    case DuplicateMessage => stats.addDupe()
    case CdrMessage => stats.addCdr()
    case CdrVsaMessage => stats.addVsa()
    case ProvideStatistics => sender ! stats
    case SessionSize(size) => stats.size = size
  }
}

sealed trait StatisticsMessage

case object DuplicateMessage extends StatisticsMessage
case object CdrVsaMessage extends StatisticsMessage
case object CdrMessage extends StatisticsMessage
case object ResetStatistics extends StatisticsMessage
case object ProvideStatistics extends StatisticsMessage
case class SessionSize(size: Int) extends StatisticsMessage