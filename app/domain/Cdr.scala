package domain

import org.joda.time.{LocalDateTime, DateTime}

trait Enum[A] {
  trait Value {
    self: A =>
  }

  val values: List[A]
  def exists(name: String) = values.find(e => e.toString == name).isDefined
  def withName(name: String) = (values.find(e => e.toString == name)).get
}

sealed trait FeatureName extends FeatureName.Value
object FeatureName extends Enum[FeatureName] {
  case object CFA extends FeatureName
  case object CFBY extends FeatureName
  case object CFNA extends FeatureName
  case object BXFER extends FeatureName
  case object CXFER extends FeatureName
  case object HOLD extends FeatureName
  case object RESUME extends FeatureName
  case object TWC extends FeatureName
  val values = List(CFA, CFBY, CFNA, BXFER, CXFER, HOLD, RESUME, TWC)
}

sealed trait FeatureStatus extends FeatureStatus.Value
object FeatureStatus extends Enum[FeatureStatus] {
  case object SUCCESS extends FeatureStatus
  case object FAIL extends FeatureStatus
  val values = List(SUCCESS, FAIL)
}

sealed trait ForwardingReason extends ForwardingReason.Value {
  def rawValue: String
}

object ForwardingReason extends Enum[ForwardingReason] {

  case object UNDEFINED extends ForwardingReason {
    def rawValue = ""
  }

  case object UNKNOWN extends ForwardingReason {
    def rawValue = "0"
  }

  case object CALL_FWD extends ForwardingReason {
    def rawValue = "1"
  }

  case object CALL_FWD_BUSY extends ForwardingReason {
    def rawValue = "2"
  }

  case object CALL_FWD_NO_REPLY extends ForwardingReason {
    def rawValue = "3"
  }

  case object CALL_DEFLECTION extends ForwardingReason {
    def rawValue = "4"
  }

  def forRawValue(rawValue: String) = {
    values.find(_.rawValue == rawValue) match {
      case Some(reason) => reason
      case None =>  { println("Unknown value: " + rawValue);UNKNOWN}
    }
  }

  val values = List(UNKNOWN, CALL_FWD, CALL_FWD_BUSY, CALL_FWD_NO_REPLY, CALL_DEFLECTION)
}

abstract class AbstractCdr(connectionId: String)

case class Cdr(id: Option[Long] = None,
               connectionId: String,
               callLegType: Int,
               setUpTime: DateTime,
               peerAddress: String,
               peerSubAddress: String,
               disconnectCause: String,
               disconnectText: String,
               connectTime: DateTime,
               disconnectTime: DateTime,
               callOrigin: String,
               chargedUnits: String,
               infoType: String,
               transmitPackets: Long,
               transmitBytes: Long,
               receivedPackets: Long,
               receivedBytes: Long,
               originalRecord: String) extends AbstractCdr(connectionId)

case class CdrVsa(cdrVsaId: Option[Long] = None,
                  connectionId: String,
                  featureId: Long,
                  legId: String,
                  name: FeatureName,
                  forwardFromNumber: String = "",
                  status: FeatureStatus,
                  featureTime: LocalDateTime,
                  forwardingReason: ForwardingReason = ForwardingReason.UNDEFINED,
                  forwardedNumber: String = "",
                  forwardSourceNumber: String = "",
                  forwardToNumber: String = "",
                  calledNumber: String,
                  callingNumber: String,
                  originalRecord: String) extends AbstractCdr(connectionId)
