import sbt._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "otp"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    javaCore,
    javaJdbc,
    javaEbean,
    "org.springframework" % "spring-context" % "3.2.0.RELEASE",
    "org.mindrot" % "jbcrypt" % "0.3m"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    requireJs += "main.javascripts"
  )

}
