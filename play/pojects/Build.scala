import sbt._
import PlayProject._

object ApplicationBuild extends Build {

  val appName = "ucm"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "mysql" % "mysql-connector-java" % "5.1.21",
    "org.springframework" % "spring-asm" % "3.0.7.RELEASE",
    "org.springframework" % "spring-beans" % "3.0.7.RELEASE",
    "org.springframework" % "spring-core" % "3.0.7.RELEASE",
    "org.springframework" % "spring-context" % "3.0.7.RELEASE",
    "org.springframework" % "spring-expression" % "3.0.7.RELEASE"
  )

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    // Add your own project settings here
  )

}