package dataimport

import org.joda.time.LocalDateTime

case class DataImportStatus(startTime: Option[LocalDateTime] = None, endTime: Option[LocalDateTime] = None) {
  def finished = endTime.isDefined
}
