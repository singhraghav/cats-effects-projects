package domain

import java.util.UUID

object category {
  case class Category(id: UUID, name: String)

  case class DeleteCategory(id: String)
}
