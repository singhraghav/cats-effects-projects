package modules

import cats.effect.IO
import http.routes.{BrandRoutes, CategoryRoutes, UserRoutes}
import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.server.middleware.{AutoSlash, RequestLogger, ResponseLogger, Timeout}
import org.typelevel.log4cats.Logger
import cats.syntax.all._
import org.http4s.implicits._

import scala.concurrent.duration.DurationInt
object HttpApi {

  def make(implicit logger: Logger[IO], services: Services[IO]): HttpApi =
    new HttpApi {}
}

sealed abstract class HttpApi(implicit logger: Logger[IO], services: Services[IO]) {

  private val userRoutes: HttpRoutes[IO] = UserRoutes(logger).routes

  private val brandRoutes: HttpRoutes[IO] = BrandRoutes(services.brands, logger).routes

  private val categoryRoutes: HttpRoutes[IO] = CategoryRoutes(services.categories, logger).routes

  private val middleware: HttpRoutes[IO] => HttpRoutes[IO] = {
    { http: HttpRoutes[IO] =>
      AutoSlash(http)
    } andThen { http: HttpRoutes[IO] =>
      Timeout(10.seconds)(http)
    }
  }

  private val loggers: HttpApp[IO] => HttpApp[IO] = {
    { http: HttpApp[IO] =>
      RequestLogger.httpApp(logHeaders = true, logBody = true)(http)
    } andThen { http: HttpApp[IO] =>
      ResponseLogger.httpApp(logHeaders = true, logBody = true)(http)
    }
  }

  private val allRoutes = userRoutes <+> brandRoutes <+> categoryRoutes

  val httpApp: HttpApp[IO] = loggers(middleware(allRoutes).orNotFound)
}
