package controllers.dataimport

import play.api.mvc.{Action, Controller}
import dataimport.DataImportManager
import play.api.libs.json.Json.toJson
import org.joda.time.format.DateTimeFormat

object ImportController extends Controller {
  val DateFormatter = DateTimeFormat.forPattern("YYYY-MM-dd hh:mm:ss")

  def index = Action {
    Ok(views.html.dataimport_page())
  }

  def triggerImport = Action {
    if (DataImportManager.schedule()) {
      Ok(toJson(Map("status" -> "OK")))
    } else {
      Status(409)
    }
  }

  def fetchStatus() = Action {
    request =>
      DataImportManager.status() match {
        case Some(status) => Ok(toJson(
          Map("finished" -> status.finished.toString,
            "start" -> (status.start.toString(DateFormatter)),
            "end" -> (status.end match {
              case Some(date) => date.toString(DateFormatter)
              case None => "--"
            }),
            "cdr" -> status.cdrCount.toString,
            "vsa" -> status.vsaCount.toString,
            "dupes" -> status.dupeCount.toString))
        )
        case None => NotFound
      }
  }
}
