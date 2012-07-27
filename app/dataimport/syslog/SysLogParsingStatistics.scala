package dataimport.syslog

class SysLogParsingStatistics {
  var warnings: Int = 0
  var errors: Int = 0
  var success: Int = 0

  def addError() {
    errors = errors + 1
  }

  def addSuccess() {
    success = success + 1
  }

  def addWarning() {
    warnings = warnings + 1
  }
}
