package controllers

sealed trait ActivePage {
  val stuff: String
  def matches(page:String) = page == stuff
}

case object ImportAdmin extends ActivePage {
  val stuff = "importAdmin"
}

case object Reporting extends ActivePage {
  val stuff = "reporting"
}



