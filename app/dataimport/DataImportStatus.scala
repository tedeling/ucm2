package dataimport

import org.joda.time.LocalDateTime

case class DataImportStatus(startTime: LocalDateTime, endTime: LocalDateTime = new LocalDateTime(), finished: Boolean = false)
