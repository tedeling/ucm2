package controllers.dataimport

import play.api.mvc.{Action, Controller}
import dataimport.{DataImportStatus, DataImportManager}

object ImportController extends Controller {
  def index = Action {
    val status: DataImportStatus = DataImportManager.status()

    Ok(views.html.status_page(if (status.finished) { "finished" } else if (status.started) { "started" } else { "idle"}))
  }

  def triggerImport = Action {
    DataImportManager.schedule()
    Ok(views.html.dataimport_page())
  }

  def status = Action {
    val status: DataImportStatus = DataImportManager.status()

    Ok(views.html.status_page(if (status.finished) { "finished" } else if (status.started) { "started" } else { "idle"}))

  }
}
