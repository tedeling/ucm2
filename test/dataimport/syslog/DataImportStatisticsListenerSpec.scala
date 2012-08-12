package dataimport.syslog

import org.specs2.mutable.Specification
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import akka.actor.{Props, ActorSystem}
import akka.util.duration._
import akka.util.Duration
import java.util.concurrent.TimeUnit
import org.specs2.ScalaCheck
import org.specs2.time.NoTimeConversions
import org.specs2.matcher.ShouldMatchers

class DataImportStatisticsListenerSpec extends FunSpec with BeforeAndAfterAll with ShouldMatchers with TestKit {

//  extends TestKit(ActorSystem()) with Specification {

  "DataImportStatisticsListener" should {
    "reset its statistics" in {
      within(Duration(5, TimeUnit.SECONDS)) {
        val actorRef = TestActorRef[DataImportStatisticsListener]

        actorRef ! ResetStatistics

        expectNoMsg()

        false
      }
    }

  }

}

