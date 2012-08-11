package dataimport

import akka.actor.ActorRef

sealed trait DataImportMessage
case object  Status extends DataImportMessage
case object TriggerDataImport extends DataImportMessage
case class SysLogImport(statisticsListener:ActorRef) extends DataImportMessage
case object SysLogResult extends DataImportMessage