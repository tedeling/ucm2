package report.dao.dto

import org.joda.time.DateTime

case class AgentStatisticsRow(summaryTime: DateTime,
                              onlineAgents:java.lang.Integer= 0,
                              avgCallLength: java.lang.Integer= 0, longestCallLength: java.lang.Integer= 0,
                              avgHoldTime: java.lang.Double = 0, longestHoldTime: java.lang.Integer = 0) {
  def < (other: AgentStatisticsRow) = this.summaryTime isBefore other.summaryTime
}