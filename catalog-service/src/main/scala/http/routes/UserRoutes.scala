package http.routes

import cats.effect.IO
import org.http4s._
import cats._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import cats.syntax.all._
import domain.user.CreateUser
import org.typelevel.log4cats.Logger

final case class UserRoutes(logger: Logger[IO]) extends Http4sDsl[IO] {

  private[routes] val prefixPath = "/auth"

  private val httpRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "users" =>
      req.as[String].flatMap { body =>
        println(s"Received request with body $body")
        Ok("created user")
      }

    case req @ GET -> Root / "users" =>
      logger.info("new request received") *>
      Ok("hello from server")
  }

  val routes: HttpRoutes[IO] = Router(
    prefixPath -> httpRoutes
  )
}
