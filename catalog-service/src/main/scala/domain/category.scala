package domain

import io.circe.generic.JsonCodec
import java.util.UUID

object category {

  @JsonCodec
  case class Category(id: UUID, name: String)

  @JsonCodec
  case class CreateCategory(name: String)

}
