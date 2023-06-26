package domain

object category {
  case class Category(id: String, name: String)

  case class DeleteCategory(id: String)
}
