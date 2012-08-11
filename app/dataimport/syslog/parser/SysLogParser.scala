package dataimport.syslog.parser

import domain.{AbstractCdr, CdrVsa, Cdr}

object SysLogParser {
  def parse(syslog: String): Option[AbstractCdr] = {
    val rawCdr = RawCdr(syslog)

    rawCdr.cdrType() match {
      case Some(cdrType) if (cdrType.contains("VOIP_CALL_HISTORY")) => cdrHistoryParser(rawCdr)
      case Some(cdrType) if (cdrType.contains("VOIP_FEAT_HISTORY")) => vsaParser(rawCdr)
      case _ => None
    }
  }

  def cdrHistoryParser(rawCdr: RawCdr): Option[Cdr] = {

    val builder = new CdrBuilder(rawCdr.cdr)

    val splittedCdr = rawCdr.splitWithoutType()

    splittedCdr map (keyValue => {
      builder.parse(keyValue)
    })

    builder.build()
  }

  def vsaParser(rawCdr: RawCdr): Option[CdrVsa] = {
    val builder = new CdrVsaBuilder(rawCdr.cdr)

    val splittedCdr = rawCdr.splitWithoutType()

    splittedCdr map (keyValue => {
      builder.parse(keyValue)
    })

    builder.build()
  }
}
