package dataimport

import akka.actor.{Props, ActorRef, Actor}
import akka.routing.RoundRobinRouter

class DataImportMaster(nrOfWorkers: Int) extends Actor {
  val workerRouter = context.actorOf(Props[DataImportWorker].withRouter(RoundRobinRouter(nrOfWorkers)), name = "workerRouter")

  def receive = {
    case TriggerDataImport => println("triggered!")

  }
}
