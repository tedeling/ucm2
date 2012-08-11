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
    DataImportManager.schedule()
    Ok(toJson(Map("status" -> "OK")))
  }

  def fetchStatus() = Action {
    request =>
      val status = DataImportManager.status()
      Ok(toJson(
        Map("finished" -> status.finished.toString,
          "start" -> (status.startTime.toString(DateFormatter)),
          "end" -> (status.endTime match {
            case Some(date) => date.toString(DateFormatter)
            case None => "--"
          }),
          "cdr" -> status.cdr.toString,
          "vsa" -> status.vsa.toString,
          "dupes" -> status.dupes.toString))
      )
  }
}
