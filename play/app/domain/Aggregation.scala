package domain

import org.joda.time.DateTime


abstract class AbstractSummary(id: Int, huntGroupId: Int, summaryTime: DateTime)

case class Agent(agentId: Int)

case class AgentSummary(id: Int,
                        huntGroupId: Int,
                        summaryTime: DateTime,
                        agentId: Int,
                        directCallsAnswered: Int,
                        directAvgTimeInCall: Int,
                        directLongestTimeInCall: Int,
                        directTotalCallsOnHold: Int,
                        directAvgHoldTime: Int,
                        directLongestHoldTime: Int,
                        queueCallsAnswered: Int,
                        queueAvgTimeInCall: Int,
                        queueLongestTimeInCall: Int,
                        queueTotalCallsOnHold: Int,
                        queueAvgHoldTime: Int,
                        queueLongestHoldTime: Int) extends AbstractSummary(id, huntGroupId, summaryTime)


case class HuntGroup(huntGroupId: Int, pilotNumber: Int) {
  import collection.mutable.ListBuffer

  val agentSummaries = ListBuffer[AgentSummary]()
  val queueSummaries  = new ListBuffer[QueueSummary]()
  val huntGroupSummaries = new ListBuffer[HuntGroupSummary]()

  def add(summary: AgentSummary) { agentSummaries += summary }
  def add(summary: QueueSummary) { queueSummaries += summary }
  def add(summary: HuntGroupSummary) { huntGroupSummaries += summary }
}

case class HuntGroupSummary(id: Int,
                            huntGroupId: Int,
                            summaryTime: DateTime,
                            agentCountMax: Int,
                            agentCountMin: Int,
                            callCount: Int,
                            answeredCount: Int,
                            abandonedCount: Int,
                            avgTimeToAnswer: Int,
                            longestTimeToAnswer: Int,
                            avgTimeInCall: Int,
                            longestTimeInCall: Int,
                            avgTimeBeforeAbandonment: Int,
                            callsOnHold: Int,
                            avgTimeOnHold: Int,
                            longestTimeOnHold: Int) extends AbstractSummary(id, huntGroupId, summaryTime)

case class QueueSummary(id: Int,
                        huntGroupId: Int,
                        summaryTime: DateTime,
                        presentedCalls: Int,
                        answeredCalls: Int,
                        callsInQueue: Int,
                        avgTimeToAnswer: Int,
                        longestTimeToAnswer: Int,
                        abandonedCalls: Int,
                        avgTimeBeforeAbandonment: Int,
                        callsFwdToVoiceMail: Int,
                        callsAnsweredByVoiceMail: Int) extends AbstractSummary(id, huntGroupId, summaryTime)