package dataimport.result

import akka.actor.Actor

sealed trait StatisticsMessage
case object DuplicateMessage extends StatisticsMessage
case object CdrVsaMessage extends StatisticsMessage
case object CdrMessage extends StatisticsMessage

class DataImportStatisticsListener extends Actor {
  override def receive = {

  }
}
