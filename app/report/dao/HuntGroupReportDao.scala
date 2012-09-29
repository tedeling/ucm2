package report.dao

import play.api.db._
import play.api.Play.current
import domain.{ CdrVsa, Cdr }
import java.sql.{ Connection, Timestamp }
import org.joda.time.DateTime
import report.dto.AgentStatisticsRow
import java.sql.ResultSet
import util.DbUtil


object HuntGroupReportDao {
  def aggregateAgents(): List[AgentStatisticsRow] = {
    DB.withConnection {
      implicit c =>
        val stmt = c.prepareStatement("""
          SELECT DATE_FORMAT(AGS.SUMMARY_TIME, '%Y%m%d') AS STIME,
                MAX(AGS.SUMMARY_TIME) AS SUMMARY_TIME,
                COUNT(DISTINCT AGS.AGENT_ID) AS ONLINE_AGENTS,
                AVG(AGS.QUEUE_AVG_TIME_IN_CALL) AS AVG_TIME_IN_CALL,
                MAX(AGS.QUEUE_LONGEST_TIME_IN_CALL) AS LONGEST_TIME_IN_CALL,
                AVG(AGS.QUEUE_AVG_HOLD_TIME) AS AVG_HOLD_TIME,
                MAX(AGS.QUEUE_LONGEST_HOLD_TIME) AS LONGEST_HOLD_TIME
        FROM AGENT_SUMMARY AGS
        GROUP BY STIME
        ORDER BY SUMMARY_TIME""")

        try {
          val resultSet = stmt.executeQuery()

          DbUtil.parseResultSet(resultSet, rs => new AgentStatisticsRow(new DateTime(rs.getTimestamp("SUMMARY_TIME")),
            rs.getInt("ONLINE_AGENTS"),
            rs.getInt("AVG_TIME_IN_CALL"),
            rs.getInt("LONGEST_TIME_IN_CALL"),
            rs.getInt("AVG_HOLD_TIME"), rs.getInt("LONGEST_HOLD_TIME")))

        } finally {
          stmt.close()
        }
    }
  }
}
