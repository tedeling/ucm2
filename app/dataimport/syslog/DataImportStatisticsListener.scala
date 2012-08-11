package dataimport.syslog

import akka.actor.Actor

class DataImportStatistics
{
  var cdr: Int = 0
  var vsa: Int = 0
  var dupes: Int = 0
}

class DataImportStatisticsListener extends Actor {
  var stats = new DataImportStatistics

  override def receive = {
    case DuplicateMessage => {
      stats.dupes = stats.dupes + 1
    }
    case CdrMessage => {
      stats.cdr = stats.cdr + 1
    }
    case CdrVsaMessage => {
      stats.vsa = stats.vsa + 1
    }

    case ResetStatistics => {
      stats = new DataImportStatistics
    }

    case ProvideStatistics => stats
  }
}

sealed trait StatisticsMessage

case object DuplicateMessage extends StatisticsMessage
case object CdrVsaMessage extends StatisticsMessage
case object CdrMessage extends StatisticsMessage
case object ResetStatistics extends StatisticsMessage
case object ProvideStatistics extends StatisticsMessage
