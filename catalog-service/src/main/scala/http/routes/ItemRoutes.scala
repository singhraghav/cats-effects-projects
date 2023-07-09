package http.routes

import cats.effect.IO
import domain.ResourceCreated
import domain.item.{ CreateItem, ItemSearchQueryParam }
import domain.jsonc._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.HttpRoutes
import org.typelevel.log4cats.Logger
import services.Items

final case class ItemRoutes(items: Items[IO], logger: Logger[IO]) extends Http4sDsl[IO] {

  private[routes] val prefixPath = "/items"

  private val httpRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "create" =>
      for {
        itemToCreate    <- req.as[CreateItem]
        generatedItemId <- items.create(itemToCreate)
        response        <- Ok(ResourceCreated(generatedItemId))
      } yield response

    case GET -> Root / "get" :? params =>
      Ok(items.search(ItemSearchQueryParam(params)))

  }

  val routes: HttpRoutes[IO] = Router(
    prefixPath -> httpRoutes
  )
}
