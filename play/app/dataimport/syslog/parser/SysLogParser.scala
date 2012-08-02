package dataimport.syslog.parser

import domain.{CdrVsa, Cdr, SysLog}
import dataimport.syslog.SysLogParsingStatistics

object SysLogParser {
  def parse(syslog: String)(implicit stats: SysLogParsingStatistics) {
    val rawCdr = RawCdr(syslog)

    rawCdr.cdrType() match {
      case Some(cdrType) if (cdrType.contains("VOIP_CALL_HISTORY")) => persist(cdrHistoryParser(rawCdr))
      case Some(cdrType) if (cdrType.contains("VOIP_FEAT_HISTORY")) => persistVsa(vsaParser(rawCdr))
      case _ => None
    }
  }

  def persist(cdr: Option[Cdr]) {
//    cdr match {
//      case Some(c) => {
//        val record = CdrDao.findByOriginalRecord(c.originalRecord)
//
//        if (record.isEmpty)
//          CdrDao.persistCdr(c)
//        else
//          LOG.warn("Record already exists %s".format(c.originalRecord))
//      }
//      case None =>
//    }
  }

  def persistVsa(cdr: Option[CdrVsa]) {
//    cdr match {
//      case Some(c) => {
//        val record = CdrDao.findByOriginalRecord(c.originalRecord)
//
//        if (record.isEmpty)
//          CdrVsaDao.persistCdr(c)
//        else
//          LOG.warn("Record already exists %s".format(c.originalRecord))
//      }
//      case None =>
//    }
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
