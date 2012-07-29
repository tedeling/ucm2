package dataimport.syslog.parser

import collection.mutable.{Map => MutableMap}
import play.api.Logger
import dataimport.syslog.SysLogParsingStatistics
import domain.Cdr
import dataimport.util.DateFormatter

class CdrBuilder(val rawCdr: String) extends BuilderMap(separator = " ") {
//  private val LOG: Logger = Logger.getLogger(classOf[CdrBuilder])

  val InvalidConnectionId = "0000"

  def build()(implicit stats: SysLogParsingStatistics): Option[Cdr] = {
    if (!cdr.contains("connectionid")) {
      stats.addError()
      Logger.error("Failed to parse CDR, no connectionId found %s".format(rawCdr))
      None
    } else if (cdr("connectionid") == InvalidConnectionId) {
      stats.addWarning()
//      Logger.warn("Discarding connectionId: 0000. CDR: %s".format(rawCdr))
      None
    } else {
      val someCdr = Some(Cdr(connectionId = cdr("connectionid"),
        callLegType = cdr("calllegtype").toInt,
        setUpTime = DateFormatter.parseDateAndTime(cdr("setuptime")),
        peerAddress = cdr.getOrElse("peeraddress", ""),
        peerSubAddress = cdr.getOrElse("peersubaddress", ""),
        disconnectCause = cdr("disconnectcause"),
        disconnectText = cdr("disconnecttext"),
        connectTime = DateFormatter.parseDateAndTime(cdr("connecttime")),
        disconnectTime = DateFormatter.parseDateAndTime(cdr("disconnecttime")),
        callOrigin = cdr.getOrElse("callorigin", ""),
        chargedUnits = cdr.getOrElse("chargedunits", ""),
        infoType = cdr.getOrElse("infotype", ""),
        transmitPackets = cdr.getOrElse("transmitpackets", "0").toLong,
        transmitBytes = cdr.getOrElse("transmitbytes", "0").toLong,
        receivedPackets = cdr.getOrElse("receivepackets", "0").toLong,
        receivedBytes = cdr.getOrElse("receivebytes", "0").toLong,
        originalRecord = rawCdr))
      stats.addSuccess()
      someCdr
    }
  }
}
