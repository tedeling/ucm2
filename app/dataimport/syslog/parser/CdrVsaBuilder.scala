package dataimport.syslog.parser

import scala.Some
import domain.{FeatureStatus, ForwardingReason, FeatureName, CdrVsa}
import play.api.Logger
import dataimport.util.DateFormatter

class CdrVsaBuilder(val rawCdr: String) extends BuilderMap(separator = ":") {

  val InvalidConnectionId = "0000"

  def build(): Option[CdrVsa] = {
    if (!cdr.contains("fcid")) {
      Logger.error("Failed to parse CDR VSA, no connectionId found in %s".format(rawCdr))
      None
    } else if (cdr("fcid") == InvalidConnectionId) {
      None
    } else {
      val rawName: String = cdr("fn")

      if (FeatureName.exists(rawName)) {
        val someCdrVsa = Some(CdrVsa(name = FeatureName.withName(rawName),
          featureTime = DateFormatter.parseDateAndTimeNoTimeZone(cdr("ft")),
          legId = cdr.getOrElse("legid", ""),
          forwardingReason = cdr.get("frson") match {
            case Some(n) => ForwardingReason.forRawValue(n)
            case None => ForwardingReason.UNDEFINED
          },
          status = cdr.get("frs") match {
            case Some("0") => FeatureStatus.SUCCESS
            case Some("1") => FeatureStatus.FAIL
            case None => {
              Logger.warn("No feature status found, assuming fail")
              FeatureStatus.FAIL
            }
          },
          featureId = cdr("fid").toLong,
          connectionId = cdr("fcid"),
          forwardedNumber = cdr.getOrElse("fwdee", ""),
          forwardSourceNumber = cdr.getOrElse("fwder", ""),
          forwardToNumber = cdr.getOrElse("fwdto", ""),
          forwardFromNumber = cdr.getOrElse("frm", ""),
          calledNumber = cdr.getOrElse("cdn", ""),
          callingNumber = cdr.getOrElse("cgn", ""),
          originalRecord = rawCdr))
        someCdrVsa
      } else {
        Logger.warn("Unknown VSA name '%s'".format(rawName))
        None
      }
    }
  }

}
