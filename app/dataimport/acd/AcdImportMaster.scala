package dataimport.acd

import akka.actor.{Props, Actor}
import akka.routing.RoundRobinRouter
import akka.util.Timeout
import dataimport.syslog.SessionSize
import play.api.Logger
import dataimport.TriggerSysLogImport

class AcdImportMaster extends Actor {
  protected def receive = {
    case TriggerAcdImport =>
  }
}

sealed trait AcdImportMasterMessage
case object TriggerAcdImport extends AcdImportMasterMessage