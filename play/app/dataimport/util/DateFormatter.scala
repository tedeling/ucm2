package dataimport.util

import org.joda.time.format.{DateTimeFormatterBuilder, DateTimeFormatter}
import org.joda.time.{DateTimeZone, DateTime}

object DateFormatter {
  val WithTimeZoneWithoutMs = new DateTimeFormatterBuilder()
    .appendHourOfDay(2)
    .appendLiteral(':')
    .appendMinuteOfHour(2)
    .appendLiteral(':')
    .appendSecondOfMinute(2)
    .appendLiteral(' ')
    .appendMonthOfYearShortText
    .appendLiteral(' ')
    .appendDayOfMonth(2)
    .appendLiteral(' ')
    .appendYear(2, 4).toFormatter

  val WithTimeZone = new DateTimeFormatterBuilder()
    .appendHourOfDay(2)
    .appendLiteral(':')
    .appendMinuteOfHour(2)
    .appendLiteral(':')
    .appendSecondOfMinute(2)
    .appendLiteral('.')
    .appendMillisOfSecond(3)
    .appendLiteral(' ')
    .appendMonthOfYearShortText
    .appendLiteral(' ')
    .appendDayOfMonth(2)
    .appendLiteral(' ')
    .appendYear(2, 4).toFormatter

  val NoTimeZone = new DateTimeFormatterBuilder()
    .appendMonthOfYear(2)
    .appendLiteral('/')
    .appendDayOfMonth(2)
    .appendLiteral('/')
    .appendYear(2, 4)
    .appendLiteral(' ')
    .appendHourOfDay(2)
    .appendLiteral(':')
    .appendMinuteOfHour(2)
    .appendLiteral(':')
    .appendSecondOfMinute(2)
    .appendLiteral('.')
    .appendMillisOfSecond(3).toFormatter

  /**
   * Parses date in format "10:14:57.378 CET Thu Dec 16 2010"
   */
  def parseDateAndTime(value: String) = parseDateAndTimeWithTz(value, WithTimeZone)

  def parseDateAndTimeWithoutMs(value: String) = parseDateAndTimeWithTz(value, WithTimeZoneWithoutMs)

  private def parseDateAndTimeWithTz(value: String, formatter: DateTimeFormatter): DateTime = {
    val splittedText: Array[String] = value.split(" ")

    val parseByJoda = "%s %s %s %s".format(splittedText(0), splittedText(3), splittedText(4), splittedText(5))
    val timeZone: DateTimeZone = DateTimeZone.forID(splittedText(1))
    formatter.parseDateTime(parseByJoda).withZone(timeZone)
  }

  def parseDateAndTimeNoTimeZone(value: String) = NoTimeZone.parseDateTime(value).toLocalDateTime
}
