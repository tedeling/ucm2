package dataimport

import akka.actor.Props
import play.api.libs.concurrent.Akka
import play.api.Play.current

object DataImportManager {

  def schedule() {
    val importMaster = Akka.system.actorOf(Props(new DataImportMaster(2)), name = "dataimport")

    importMaster ! TriggerDataImport
  }
}
