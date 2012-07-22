package controllers.dataimport

import org.specs2.mutable.Specification
import helpers.TestHelpers._
import play.api.test.Helpers._
import play.api.test.FakeRequest

class ImportControllerSpec extends Specification {
  "Import controller" should {
    "render" in {
      runInServer {
        val Some(result) = routeAndCall(FakeRequest(GET, "/import"))

        status(result) must equalTo(OK)
      }
    }
  }
}
