package domain

import domain.brand.Brand
import domain.category.Category

import java.util.UUID

object item {

  case class Item(id: UUID, name: String, brand: Brand, quantity: Int, categories: List[Category])

  case class CreateItem(name: String, brandId: String, quantity: Int, category: List[String])

  case class UpdateItemName(id: String, newName: String)

  case class UpdateItemBrand(id: String, newBrand: String)

  case class UpdateItemCategory(id: String, category: List[String])

}
