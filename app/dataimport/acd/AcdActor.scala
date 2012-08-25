package dataimport.acd

import akka.actor.{Props, Actor}
import akka.routing.RoundRobinRouter
import akka.util.Timeout
import dataimport.syslog.SessionSize
import play.api.Logger
import dataimport.SysLogImport

class AcdImportMaster extends Actor {
  protected def receive = {

  }
}