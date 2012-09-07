package dataimport.syslog

import akka.actor.Actor
import org.joda.time.LocalDateTime

class SysLogImportStatistics {
  private var cdr: Int = 0
  private var vsa: Int = 0
  private var dupes: Int = 0
  private val startTime: LocalDateTime = new LocalDateTime()
  private var endTime: Option[LocalDateTime] = None
  private[syslog] var size: Int = _

  def cdrCount = cdr
  def vsaCount = vsa
  def dupeCount = dupes
  def start = startTime
  def end = endTime
  def rowsFound = size
  def recordsPerSecond = {
   val sessionLength= (new LocalDateTime().toDate().getTime() - startTime.toDate().getTime()) / 1000
   
   if (sessionLength != 0) {
	   (cdr + vsa + dupes) / sessionLength
   } else {
     0
   }
  }

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

class SysLogImportStatisticsListener extends Actor {
  var stats: Option[SysLogImportStatistics] = None

  override def receive = {
    case ResetStatistics => stats = Some(new SysLogImportStatistics())
    case DuplicateMessage => stats.get.addDupe()
    case CdrMessage => stats.get.addCdr()
    case CdrVsaMessage => stats.get.addVsa()
    case ProvideStatistics => sender ! stats
    case RecordFound(size) => stats.get.size = stats.get.size + size
  }
}

sealed trait SysLogImportStatsMessage
case object DuplicateMessage extends SysLogImportStatsMessage
case object CdrVsaMessage extends SysLogImportStatsMessage
case object CdrMessage extends SysLogImportStatsMessage
case object ResetStatistics extends SysLogImportStatsMessage
case object ProvideStatistics extends SysLogImportStatsMessage
case class RecordFound(size: Int) extends SysLogImportStatsMessage
