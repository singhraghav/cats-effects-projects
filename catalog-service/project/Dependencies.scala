import sbt.*
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport.*
object Dependencies {

  object V {
    val zerowaste  = "0.2.5"
    val cats       = "2.9.0"
    val catsEffect = "3.4.8"
    val circe      = "0.14.4"
    val http4s     = "1.0.0-M39"
    val log4cats   = "2.3.1"
  }
  object Libraries {
    def circe(artifact: String) = "io.circe" %% s"circe-$artifact" % V.circe

    def http4s(artifact: String): ModuleID = "org.http4s" %% s"http4s-$artifact" % V.http4s

    val cats       = "org.typelevel" %% "cats-core"   % V.cats
    val catsEffect = "org.typelevel" %% "cats-effect" % V.catsEffect

    val circeCore    = circe("core")
    val circeParser  = circe("parser")
    val circeRefined = circe("refined")

    val http4sDsl    = http4s("dsl")
    val http4sServer = http4s("ember-server")
    val http4sClient = http4s("ember-client")
    val http4sCirce  = http4s("circe")

    val log4cats = "org.typelevel" %% "log4cats-slf4j" % V.log4cats

  }

  object CompilerPlugins {
    val zerowaste = compilerPlugin("com.github.ghik" % "zerowaste" % V.zerowaste cross CrossVersion.full)
  }

}
