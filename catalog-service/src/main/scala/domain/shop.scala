package domain

object shop {
  case class Shop(id: String, name: String, owner: String)

  case class CreateShop(name: String, owner: String)

  case class DeleteShop(id: String)
}
