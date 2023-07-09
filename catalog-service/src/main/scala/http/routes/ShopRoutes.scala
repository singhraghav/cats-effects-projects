package http.routes

import cats.effect.IO
import domain.shop.CreateShop
import org.typelevel.log4cats.Logger
import services.ShopsMetaData
import domain.ResourceCreated
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import domain.jsonc._
import org.http4s.{ HttpRoutes, InvalidMessageBodyFailure, Response }
import org.postgresql.util.PSQLException

import java.util.UUID
final case class ShopRoutes(shops: ShopsMetaData[IO], logger: Logger[IO]) extends Http4sDsl[IO] {

  private[routes] val prefixPath = "/shop"

  private object ShopByNameQueryParam extends QueryParamDecoderMatcher[String]("name")

  private object ShopByIdQueryParam extends QueryParamDecoderMatcher[UUID]("id")

  private object ShopByOwnerQueryParam extends QueryParamDecoderMatcher[UUID]("ownerId")

  private val httpRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "create" =>
      (
        for {
          shopToCreate    <- req.as[CreateShop]
          generatedShopId <- shops.create(shopToCreate)
          response        <- Ok(ResourceCreated(generatedShopId))
        } yield response
      )
      .handleErrorWith(handleUserCreationErrors)

    case GET -> Root / "get" :? ShopByNameQueryParam(name) => Ok(shops.findByName(name.trim))

    case GET -> Root / "get" :? ShopByIdQueryParam(id) => Ok(shops.findById(id))

    case GET -> Root / "get" :? ShopByOwnerQueryParam(ownerId) => Ok(shops.findByOwner(ownerId))

    case DELETE -> Root / "remove" :? ShopByIdQueryParam(shopId) => Ok(shops.removeShop(shopId))

    case GET -> Root / "get" / "all" => Ok(shops.getAll)

  }

  private val handleUserCreationErrors: PartialFunction[Throwable, IO[Response[IO]]] = {
    val duplicateInsertionError = "ERROR: duplicate key value violates unique constraint \"shop_meta_data_name_key\""

    val response: PartialFunction[Throwable, IO[Response[IO]]] = {
      case ex: InvalidMessageBodyFailure => BadRequest(ex.getCause().getMessage)
      case ex: PSQLException if ex.getMessage.contains(duplicateInsertionError) =>
        BadRequest("Shop With this name Already exist")
    }
    response
  }

  val routes: HttpRoutes[IO] = Router(
    prefixPath -> httpRoutes
  )
}
