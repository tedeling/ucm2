package domain

import org.joda.time.DateTime

case class SysLog(id: Long,
                  deviceTime: DateTime,
                  facility: Int,
                  priority: Int,
                  host: String,
                  message: String)
