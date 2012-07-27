package dataimport

import akka.actor.Actor
import play.api.modules.spring.Spring
import syslog.{SysLogImporter}

class SysLogImportWorker extends Actor {
  val x:SysLogImporter = Spring.getBeanOfType(classOf[SysLogImporter])


  protected def receive =  {
    case SysLogImport => {
      x.parseSysLog()
      sender ! SysLogResult
    }
  }
}
