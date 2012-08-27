package controllers.report

import play.api.mvc.{Action, Controller}

object ReportController extends Controller {

  def index = Action {
    Ok(views.html.report_page())
  }
}
