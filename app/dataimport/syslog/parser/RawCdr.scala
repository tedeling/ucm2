package dataimport.syslog.parser

import java.util.regex.Pattern

case class RawCdr(cdr: String) {
  private final val SPLIT_PATTERN = Pattern.compile(",")

  def splitWithoutType(): Array[String] = {
    val firstElementAfterType = endOfType() + 2

    if (cdr.length > firstElementAfterType) {
      val withoutType = cdr.substring(firstElementAfterType)
      val withoutFeature = withoutType.replaceAll("FEAT_VSA=", "")
      SPLIT_PATTERN.split(withoutFeature)
    }
    else {
      new Array[String](0)
    }
  }

  private def beginOfType() = cdr.indexOf('%')

  private def endOfType() = cdr.indexOf(':', beginOfType())

  def cdrType() = {
    val begin = beginOfType()
    val end = endOfType()

    if (begin > 0 && end > 0) {
      Some(cdr.substring(begin, end))
    } else {
      None
    }
  }
}
