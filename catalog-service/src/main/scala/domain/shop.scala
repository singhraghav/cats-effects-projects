package domain

import io.circe.generic.JsonCodec

import java.util.UUID

object shop {

  @JsonCodec
  case class ShopMetaData(id: UUID, name: String, owner: UUID)

  @JsonCodec
  case class CreateShop(name: String, ownerId: UUID) {
    def toShopMetaData: ShopMetaData = ShopMetaData(UUID.randomUUID(), name.toLowerCase(), ownerId)
  }

}
