package dataimport.syslog

import akka.testkit.{TestActorRef, TestKit}
import akka.util.Duration
import java.util.concurrent.TimeUnit
import org.specs2.matcher.ShouldMatchers
import org.scalatest.{WordSpec, BeforeAndAfterAll, FunSpec}
import akka.actor.ActorSystem

class DataImportStatisticsListenerSpec extends TestKit(ActorSystem()) with WordSpec with BeforeAndAfterAll with ShouldMatchers  {

  "DataImportStatisticsListener" should {
    "reset its statistics" in {
      within(Duration(5, TimeUnit.SECONDS)) {
        val actorRef = TestActorRef[DataImportStatisticsListener]

        actorRef ! ResetStatistics

        expectNoMsg()
      }
    }
  }

}

