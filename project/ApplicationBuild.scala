import sbt._
import play.Project._
import Keys._

object ApplicationBuild extends Build {

  val appName = "otp"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    javaCore,
    javaJdbc,
    javaEbean,
    "com.google.inject" % "guice" % "3.0",
    "com.google.inject.extensions" % "guice-servlet" % "3.0",
    "com.google.inject.extensions" % "guice-multibindings" % "3.0",
    "com.google.guava" % "guava" % "13.0",
    "org.mindrot" % "jbcrypt" % "0.3m",
    "com.typesafe" % "play-plugins-mailer_2.10" % "2.1-SNAPSHOT",
    "org.picketbox" % "picketbox-core" % "5.0.0-2013Jan04"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    requireJs += "main.javascripts",

    resolvers += "JBoss repository" at "https://repository.jboss.org/nexus/content/repositories/releases"
  )

}
