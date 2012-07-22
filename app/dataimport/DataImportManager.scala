package dataimport

import akka.actor.Props
import play.api.libs.concurrent.Akka
import play.api.Play.current

class DataImportManager {

  def schedule() {
    val myActor = Akka.system.actorOf(Props[DataImportMaster], name = "dataimport")

  }
}
