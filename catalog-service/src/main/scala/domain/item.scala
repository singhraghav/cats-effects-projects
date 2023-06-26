package domain

object item {
  case class Item(name: String, brand: String, categories: List[String])

  case class UpdateItemName(id: String, newName: String)

  case class UpdateItemBrand(id: String, newBrand: String)

  case class UpdateItemCategory(id: String, category: List[String])

}
