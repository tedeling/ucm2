package dataimport.syslog.dao

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import domain.Cdr
import java.sql.{Timestamp, PreparedStatement}

object SysLogDao {
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

  def persist(cdr: Cdr)  {
    DB.withConnection { conn =>
      val stmt: PreparedStatement = conn.prepareStatement( """INSERT INTO CDR (CALL_LEG_TYPE, CONNECTION_ID, SETUP_TIME,
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
  }
}
