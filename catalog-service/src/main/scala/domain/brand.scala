package domain

object brand {
  case class Brand(id: String, name: String)

  case class CreateBrand(name: String)

}
