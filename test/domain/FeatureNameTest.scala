package domain

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class FeatureNameSpec extends Specification {
  "The FeatureName" should {

    "act as an enumeration and be resolvable" in {
      FeatureName.withName("TWC") == FeatureName.TWC
    }

    "act as an enumeration and check whether it exists" in {
      FeatureName.exists("TWC")
    }
  }
}
