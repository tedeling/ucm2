package dataimport.syslog.parser

import collection.mutable.{Map => MutableMap}

class BuilderMap(separator: String) {
  val cdr = MutableMap[String, String]()

  def parse(keyValue: String) {
    val trimmed = keyValue.trim()
    val seperatorLocation = trimmed.indexOf(separator)

    if (seperatorLocation >= 0) {
      val value = trimmed.substring(seperatorLocation + 1).trim()
      val key = trimmed.substring(0, seperatorLocation).trim()

      if (!value.isEmpty) {
        cdr(key.toLowerCase) = value
      }
    }
  }
}
