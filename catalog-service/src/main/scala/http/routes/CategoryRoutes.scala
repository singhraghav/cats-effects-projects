package http.routes

import cats.effect.IO
import domain.ResourceCreated
import domain.category.CreateCategory
import domain.jsonc._
import io.circe.generic.auto.exportEncoder
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.typelevel.log4cats.Logger
import services.Categories

final case class CategoryRoutes(categories: Categories[IO], logger: Logger[IO]) extends Http4sDsl[IO] {

  private[routes] val prefixPath = "/category"

  private object CategoryQueryParam extends OptionalQueryParamDecoderMatcher[String]("name")

  private val httpRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "create" =>
      for {
        categoriesToCreate <- req.as[CreateCategory]
        _                  <- logger.info(s"Received request to create a category $categoriesToCreate")
        id                 <- categories.create(categoriesToCreate.name)
        response           <- Ok(ResourceCreated(id))
      } yield response

    case GET -> Root / "get" :? CategoryQueryParam(brandName) =>
      Ok(brandName.fold(categories.findAll)(someBrand => categories.findByName(someBrand)))
  }

  val routes: HttpRoutes[IO] = Router(
    prefixPath -> httpRoutes
  )
}
