package helpers

import play.api.test.FakeApplication
import play.api.Play.current
import play.api.test.Helpers._

object TestHelpers {
  def runInServer[T](block: => T): T = {
    running(FakeApplication()) {
      block
    }
  }
}
