package modules

import cats.effect.IO
import http.routes.UserRoutes
import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.server.middleware.{AutoSlash, RequestLogger, ResponseLogger}
import org.typelevel.log4cats.Logger

object HttpApi {

  def make(implicit logger: Logger[IO]): HttpApi =
    new HttpApi {}
}

sealed abstract class HttpApi(implicit logger: Logger[IO]) {

  private val userRoutes: HttpRoutes[IO] = UserRoutes(logger).routes

  private val middleware: HttpRoutes[IO] => HttpRoutes[IO] = {
    { http: HttpRoutes[IO] =>
      AutoSlash(http)
    }
  }

  private val loggers: HttpApp[IO] => HttpApp[IO] = {
    { http: HttpApp[IO] =>
      RequestLogger.httpApp(logHeaders = true, logBody = true)(http)
    } andThen { http: HttpApp[IO] =>
      ResponseLogger.httpApp(logHeaders = true, logBody = true)(http)
    }
  }

  val httpApp: HttpApp[IO] = loggers(middleware(userRoutes).orNotFound)
}
