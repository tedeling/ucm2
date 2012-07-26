package dataimport.syslog.dao

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

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

        println("starting query")
        val x = SQL(query)
          .on('id -> id)
          .as(long("ID") ~ str("Message") *)
          .map(flatten)
        println("ending query")
        x
    }
  }
}
