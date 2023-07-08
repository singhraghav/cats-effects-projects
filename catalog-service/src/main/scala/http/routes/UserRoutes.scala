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
import org.http4s.{ HttpRoutes, Response, Status }

import java.util.UUID

final case class UserRoutes(users: Users[IO], logger: Logger[IO]) extends Http4sDsl[IO] {

  private[routes] val prefixPath = "/users"

  private object UserEmailQueryParam extends QueryParamDecoderMatcher[String]("email")

  private object UserIdQueryParam extends QueryParamDecoderMatcher[UUID]("id")

  private val httpRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "create" =>
      for {
        userToCreate      <- req.as[CreateUser]
        _                 <- logger.info(s"Received request to create a new user with parameters $userToCreate")
        dbInsertionResult <- users.create(userToCreate).attempt
        response          <- handleCreationResult(dbInsertionResult)
      } yield response

    case GET -> Root / "get" :? UserEmailQueryParam(email) =>
      Ok(users.findByEmail(email.trim))

    case GET -> Root / "get" :? UserIdQueryParam(id) =>
      Ok(users.findById(id))
  }

  private def handleCreationResult(dbInsertionResult: Either[Throwable, UUID]): IO[Response[IO]] = {
    val duplicateInsertionError = "ERROR: duplicate key value violates unique constraint \"users_email_key\""
    dbInsertionResult match {
      case Right(generatedUserId) => Ok(ResourceCreated(generatedUserId))
      case Left(error) if error.getMessage.contains(duplicateInsertionError) =>
        BadRequest("user with this email already exist")
      case _ => InternalServerError()
    }
  }

  val routes: HttpRoutes[IO] = Router(
    prefixPath -> httpRoutes
  )
}
