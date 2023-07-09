package domain

import io.circe.generic.JsonCodec
import java.util.UUID

object item {

  @JsonCodec
  case class Item(id: UUID, name: String, brandId: UUID, quantity: Int, shopId: UUID)

  @JsonCodec
  case class CreateItem(name: String, brandId: UUID, quantity: Int, shopId: UUID) {
    def toItem: Item = Item(UUID.randomUUID(), name.toLowerCase(), brandId, quantity, shopId)
  }

  @JsonCodec
  case class ItemSearchQueryParam(name: Option[String], brandId: Option[UUID], shopId: Option[UUID]) {
    def hasNoParameterDefined: Boolean = name.isEmpty && brandId.isEmpty && shopId.isEmpty
  }

  object ItemSearchQueryParam {

    def apply(params: Map[String, scala.collection.Seq[String]]): ItemSearchQueryParam = {
      val name    = params.get("name").flatMap(_.headOption).map(_.toLowerCase())
      val brandId = params.get("brandId").flatMap(_.headOption).map(id => UUID.fromString(id.trim))
      val shopId  = params.get("shopId").flatMap(_.headOption).map(id => UUID.fromString(id.trim))
      new ItemSearchQueryParam(name, brandId, shopId)
    }

  }

}
