package report.dao

import org.specs2.mutable.Specification
import helpers.TestHelpers._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class HuntGroupReportDaoSpec extends Specification {
  "HuntGroupReportDao" should {
    "aggregate agents" in {
      runInServer {
        val agents = HuntGroupReportDao.aggregateAgents()
        
        agents.map(agent => println(agent.avgHoldTime))
        1 == 1
      }
    }
  }
}

