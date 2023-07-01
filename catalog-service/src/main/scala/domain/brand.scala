package domain

import java.util.UUID

object brand {
  case class Brand(id: UUID, name: String)

  object Brand {
    def apply(name: String): Brand = new Brand(UUID.randomUUID(), name)
  }

  case class CreateBrand(name: String)

}
