package domain

import io.circe.generic.JsonCodec
import java.util.UUID


object brand {

  @JsonCodec
  case class Brand(id: UUID, name: String)

  object Brand {
    def apply(name: String): Brand = new Brand(UUID.randomUUID(), name)
  }

  @JsonCodec
  case class CreateBrand(name: String)

}
