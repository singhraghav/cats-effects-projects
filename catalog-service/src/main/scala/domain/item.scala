package domain

import domain.brand.Brand
import domain.category.Category

import java.util.UUID

object item {

  case class Item(id: UUID, name: String, brand: Brand, quantity: Int, categories: List[Category])

  case class CreateItemParam(name: String, brandId: String, quantity: Int, category: List[String])

  case class UpdateItemNameParam(id: String, newName: String)

  case class UpdateItemBrandParam(id: String, newBrand: String)

  case class UpdateItemCategoryParam(id: String, category: List[String])

  case class UpdateItemQuantityParam(id: String, newQuantity: Int)

}
