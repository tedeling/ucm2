package dataimport

import akka.actor.ActorRef

sealed trait SysLogImportMessage
case object  Status extends SysLogImportMessage
case object TriggerSysLogImport extends SysLogImportMessage
case class TriggerSysLogImport(statisticsListener:ActorRef) extends SysLogImportMessage
