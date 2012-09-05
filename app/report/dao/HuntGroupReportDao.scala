package dataimport.syslog.dao

import play.api.db._
import play.api.Play.current
import anorm.SqlParser._
import domain.{ CdrVsa, Cdr }
import java.sql.{ Connection, Timestamp }
import org.joda.time.DateTime
import report.dto.AgentStatisticsRow
import anorm._

object HuntGroupReportDao {
  def aggregateAgents(): Iterator[AgentStatisticsRow] = {
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
          val query = stmt.executeQuery()

          def findAll: Iterator[AgentStatisticsRow] = new Iterator[AgentStatisticsRow] {
            val rs = query

            override def hasNext = !rs.isLast

            override def next() = {
              rs.next()

              new AgentStatisticsRow(new DateTime(rs.getTimestamp("SUMMARY_TIME")),
                rs.getInt("ONLINE_AGENTS"),
                rs.getInt("AVG_TIME_IN_CALL"),
                rs.getInt("LONGEST_TIME_IN_CALL"),
                rs.getInt("AVG_HOLD_TIME"), rs.getInt("LONGEST_HOLD_TIME"))
            }
          }

          findAll.map(x => x)

        } finally {
          stmt.close()
        }
    }
  }

  def findAfterId(id: Long): List[(Long, String)] = {
    DB.withConnection {
      implicit c =>
        val query = """SELECT ID, Message
                      FROM SystemEvents
                      WHERE ID >= {id} AND ID <= 20000
                      AND Priority = 5
                      AND Facility = 5
                    """

        SQL(query)
          .on('id -> id)
          .as(long("ID") ~ str("Message") *)
          .map(flatten)
    }
  }

  def cdrExists(originalRecord: String)(implicit conn: Connection) = {
    val stmt = conn.prepareStatement("""SELECT ID FROM CDR WHERE ORIGINAL_RECORD = ?""")
    stmt.setString(1, originalRecord)
    stmt.executeQuery().next()
  }

  def persistCdr(cdr: Cdr)(implicit conn: Connection) {
    val stmt = conn.prepareStatement("""INSERT INTO CDR (CALL_LEG_TYPE, CONNECTION_ID, SETUP_TIME,
                                                      PEER_ADDRESS, PEER_SUB_ADDRESS,
                                                      DISCONNECT_CAUSE, DISCONNECT_TEXT,
                                                      CONNECT_TIME, DISCONNECT_TIME,
                                                      CALL_ORIGIN, CHARGED_UNITS, INFO_TYPE,
                                                      TRANSMIT_PACKETS, TRANSMIT_BYTES,
                                                      RECEIVED_PACKETS, RECEIVED_BYTES,
                                                      ORIGINAL_RECORD)
                                                      VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""")

    stmt.setInt(1, cdr.callLegType)
    stmt.setString(2, cdr.connectionId)
    stmt.setTimestamp(3, new Timestamp(cdr.setUpTime.getMillis))
    stmt.setString(4, cdr.peerAddress)
    stmt.setString(5, cdr.peerSubAddress)
    stmt.setString(6, cdr.disconnectCause)
    stmt.setString(7, cdr.disconnectText)
    stmt.setTimestamp(8, new Timestamp(cdr.connectTime.getMillis))
    stmt.setTimestamp(9, new Timestamp(cdr.disconnectTime.getMillis))
    stmt.setString(10, cdr.callOrigin)
    stmt.setString(11, cdr.chargedUnits)
    stmt.setString(12, cdr.infoType)
    stmt.setLong(13, cdr.transmitPackets)
    stmt.setLong(14, cdr.transmitBytes)
    stmt.setLong(15, cdr.receivedPackets)
    stmt.setLong(16, cdr.receivedBytes)
    stmt.setString(17, cdr.originalRecord)

    stmt.execute()
  }

  def vsaExists(originalRecord: String)(implicit conn: Connection) = {
    val stmt = conn.prepareStatement("""SELECT ID FROM CDR_VSA WHERE ORIGINAL_RECORD = ?""")
    stmt.setString(1, originalRecord)
    stmt.executeQuery().next()
  }

  def persistCdrVsa(vsa: CdrVsa)(implicit conn: Connection) {
    val stmt = conn.prepareStatement("""INSERT INTO CDR_VSA (FEATURE_ID, CONNECTION_ID,
                                           FEATURE_NAME, FWD_FROM_NUMBER, STATUS, FEATURE_TIME,
                                           FWD_REASON, FWD_NUMBER, FWD_SRC_NUMBER, FWD_TO_NUMBER,
                                           CALLED_NUMBER, CALLING_NUMBER,
                                           LEG_ID, ORIGINAL_RECORD)
                                         VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""")

    stmt.setLong(1, vsa.featureId)
    stmt.setString(2, vsa.connectionId)
    stmt.setString(3, vsa.name.toString)
    stmt.setString(4, vsa.forwardFromNumber)
    stmt.setString(5, vsa.status.toString)
    stmt.setTimestamp(6, new Timestamp(vsa.featureTime.toDate.getTime))
    stmt.setString(7, vsa.forwardingReason.toString)
    stmt.setString(8, vsa.forwardedNumber)
    stmt.setString(9, vsa.forwardSourceNumber)
    stmt.setString(10, vsa.forwardToNumber)
    stmt.setString(11, vsa.calledNumber)
    stmt.setString(12, vsa.callingNumber)
    stmt.setString(13, vsa.legId)
    stmt.setString(14, vsa.originalRecord)

    stmt.execute()
  }
}
