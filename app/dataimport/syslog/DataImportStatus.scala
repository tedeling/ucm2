package dataimport.syslog

import org.joda.time.LocalDateTime

case class DataImportStatus(startTime: LocalDateTime, endTime: Option[LocalDateTime] = None) {
  def finished:Boolean = endTime.isDefined
}