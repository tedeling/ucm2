package dataimport.syslog.parser

import domain.{AbstractCdr, CdrVsa, Cdr, SysLog}
import dataimport.syslog.SysLogParsingStatistics

object SysLogParser {
  def parse(syslog: String)(implicit stats: SysLogParsingStatistics): Option[AbstractCdr] = {
    val rawCdr = RawCdr(syslog)

    rawCdr.cdrType() match {
      case Some(cdrType) if (cdrType.contains("VOIP_CALL_HISTORY")) => cdrHistoryParser(rawCdr)
      case Some(cdrType) if (cdrType.contains("VOIP_FEAT_HISTORY")) => vsaParser(rawCdr)
      case _ => None
    }
  }

  def cdrHistoryParser(rawCdr: RawCdr)(implicit stats: SysLogParsingStatistics): Option[Cdr] = {

    val builder = new CdrBuilder(rawCdr.cdr)

    val splittedCdr = rawCdr.splitWithoutType()

    splittedCdr map (keyValue => {
      builder.parse(keyValue)
    })

    builder.build()
  }

  def vsaParser(rawCdr: RawCdr)(implicit stats: SysLogParsingStatistics): Option[CdrVsa] = {
    val builder = new CdrVsaBuilder(rawCdr.cdr)

    val splittedCdr = rawCdr.splitWithoutType()

    splittedCdr map (keyValue => {
      builder.parse(keyValue)
    })

    builder.build()
  }
}
