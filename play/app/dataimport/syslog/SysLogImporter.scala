package dataimport.syslog

import dao.SysLogDao
import org.springframework.stereotype.Service
import parser.SysLogParser

trait SysLogImporter {
  def parseSysLog(): SysLogParsingStatistics
}

@Service
class SysLogImporterImpl extends SysLogImporter {
  def parseSysLog(): SysLogParsingStatistics = {
    implicit val stats = new SysLogParsingStatistics()
        SysLogDao.findAfterId(0) //map (x => println(x._2))
    stats
  }
}

