package dataimport

import akka.actor.Actor


class SysLogImportWorker extends Actor {
  protected def receive =  {
    case SysLogImport => sender ! SysLogResult
  }
}
