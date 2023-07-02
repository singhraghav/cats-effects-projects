package http.routes

import cats.effect.IO
import domain.ResourceCreated
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import domain.brand.CreateBrand
import domain.jsonc._
import io.circe.generic.auto.exportEncoder
import org.typelevel.log4cats.Logger
import services.Brands

final case class BrandRoutes(brands: Brands[IO], logger: Logger[IO]) extends Http4sDsl[IO] {

  private[routes] val prefixPath = "/brand"

  private object BrandQueryParam extends OptionalQueryParamDecoderMatcher[String]("brand")

  private val httpRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "create" =>
      for {
        brandToCreate <- req.as[CreateBrand]
        _             <- logger.info(s"Received request to create a brand $brandToCreate")
        id            <- brands.create(brandToCreate.name)
        response      <- Ok(ResourceCreated(id))
      } yield response

    case GET -> Root / "get" :? BrandQueryParam(brandName) =>
      Ok(brandName.fold(brands.findAll)(someBrand => brands.findByName(someBrand)))
  }

  val routes: HttpRoutes[IO] = Router(
    prefixPath -> httpRoutes
  )
}
