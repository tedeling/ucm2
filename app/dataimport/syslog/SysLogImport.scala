package dataimport.syslog

import dao.SysLogDao
import org.springframework.stereotype.Service
import nl.tecon.ucm.dataimport.db.DbConfig
import parser.SysLogParser

trait SysLogImport {
  def parseSysLog(): SysLogParsingStatistics
}

@Service
class SysLogImportImpl extends SysLogImport {
  def parseSysLog(): SysLogParsingStatistics = {
    implicit val stats = new SysLogParsingStatistics()

        SysLogDao.findAfterId(0) map (SysLogParser.parse(_))

    stats
  }
}

