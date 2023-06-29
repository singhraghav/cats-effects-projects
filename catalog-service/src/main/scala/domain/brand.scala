package domain

import java.util.UUID

object brand {
  case class Brand(id: UUID, name: String)

  case class CreateBrand(name: String)

}
