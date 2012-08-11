package controllers.dataimport

import play.api.mvc.{Action, Controller}
import dataimport.{DataImportStatus, DataImportManager}
import play.api.libs.json.Json.toJson
import org.joda.time.LocalDate
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

  def status = Action {
    val status: DataImportStatus = DataImportManager.status()

    Ok(views.html.status_page(if (status.finished) {
      "idle"
    } else {
      "started"
    }))
  }

  def fetchStatus() = Action {
    request =>
      val status = DataImportManager.status()
      Ok(toJson(
        Map("started" -> "fe",
          "finished" -> status.finished.toString,
          "start" -> (status.startTime.getOrElse(new LocalDate()).toString(DateFormatter)))
      ))
  }
}
