package controllers.dataimport

import play.api.mvc.{Action, Controller}
import dataimport.DataImportManager

object ImportController extends Controller {
  def index = Action {
    Ok(views.html.dataimport())
  }

  def triggerImport = Action {
    DataImportManager.schedule()
    Ok(views.html.dataimport())
  }
}
