package domain

import domain.brand.Brand

object item {
  case class Item(id: String, brand: Brand)

}
