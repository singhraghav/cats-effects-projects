package domain

import io.circe.generic.JsonCodec

import java.util.UUID

object item {

  @JsonCodec
  case class ItemMetaData(id: UUID, name: String, brandId: UUID, quantity: Int, shopId: UUID, categories: List[UUID])

  case class CreateItem(name: String, brandId: UUID, quantity: Int, shopId: UUID, categories: List[UUID]) {

    def toItemMetaData: ItemMetaData = ItemMetaData(UUID.randomUUID(), name.toLowerCase(), brandId, quantity, shopId, categories)

  }

  case class UpdateItemNameParam(id: UUID, newName: String)

  case class UpdateItemBrandParam(id: UUID, newBrand: UUID)

  case class UpdateItemCategoryParam(id: UUID, newCategories: List[String])

  case class UpdateItemQuantityParam(id: UUID, newQuantity: Int)

}
