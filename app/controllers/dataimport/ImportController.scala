package controllers.dataimport

import play.api.mvc.{Action, Controller}

object ImportController extends Controller {
  def index = Action {


    Ok(views.html.dataimport())
  }
}
