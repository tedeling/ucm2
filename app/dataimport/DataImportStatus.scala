package dataimport

import org.joda.time.LocalDateTime

case class DataImportStatus(startTime: Option[LocalDateTime] = None, endTime: Option[LocalDateTime] = None, started: Boolean = false, finished: Boolean = false)
