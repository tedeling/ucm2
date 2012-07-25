package dataimport.syslog.dao

import nl.tecon.ucm.domain.{Cdr, SysLog}
import org.mybatis.scala.mapping._
import org.joda.time.DateTime
import nl.tecon.ucm.domain.Cdr
import nl.tecon.ucm.domain.SysLog


object SysLogDao {
  val SysLogResultMap = new ResultMap[SysLog] {
    id(column = "ID", property = "id")
    arg(column = "ID", javaType = T[Long])
    arg(column = "DeviceReportedTime", javaType = T[DateTime])
    arg(column = "Facility", javaType = T[Int])
    arg(column = "Priority", javaType = T[Int])
    arg(column = "FromHost", javaType = T[String])
    arg(column = "Message", javaType = T[String])
  }

  val findAfterId = new SelectListBy[Long, SysLog] {
    resultMap = SysLogResultMap

    def xsql = <xsql>
      SELECT *
      FROM SystemEvents
      WHERE ID >= #{{id}} AND Priority = 5
      AND Facility = 5
    </xsql>
  }

  def bind = Seq(findAfterId)
}
