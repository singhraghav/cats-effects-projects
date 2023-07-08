package http.routes

import cats.effect.IO
import domain.ResourceCreated
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import domain.user.CreateUser
import domain.jsonc._
import org.typelevel.log4cats.Logger
import services.Users
import org.http4s.{ HttpRoutes, InvalidMessageBodyFailure, Response }
import org.postgresql.util.PSQLException

import java.util.UUID

final case class UserRoutes(users: Users[IO], logger: Logger[IO]) extends Http4sDsl[IO] {

  private[routes] val prefixPath = "/users"

  private object UserEmailQueryParam extends QueryParamDecoderMatcher[String]("email")

  private object UserIdQueryParam extends QueryParamDecoderMatcher[UUID]("id")

  private val httpRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "create" =>
      (
        for {
          userToCreate <- req.as[CreateUser]
          _            <- logger.info(s"Received request to create a new user with parameters $userToCreate")
          userId       <- users.create(userToCreate)
          response     <- Ok(ResourceCreated(userId))
        } yield response
      ).handleErrorWith(handleUserCreationErrors)

    case GET -> Root / "get" :? UserEmailQueryParam(email) => Ok(users.findByEmail(email.trim))

    case GET -> Root / "get" :? UserIdQueryParam(id) => Ok(users.findById(id))
  }

  private val handleUserCreationErrors: PartialFunction[Throwable, IO[Response[IO]]] = {
    val duplicateInsertionError = "ERROR: duplicate key value violates unique constraint \"users_email_key\""

    val response: PartialFunction[Throwable, IO[Response[IO]]] = {
      case ex: InvalidMessageBodyFailure => BadRequest(ex.getCause().getMessage)
      case ex: PSQLException if ex.getMessage.contains(duplicateInsertionError) =>
        BadRequest("user with this email already exist")
    }
    response
  }

  val routes: HttpRoutes[IO] = Router(
    prefixPath -> httpRoutes
  )
}
