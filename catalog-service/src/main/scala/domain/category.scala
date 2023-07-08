package domain

import io.circe.{ Decoder, Encoder }
import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }

import java.util.UUID

object category {
  case class Category(id: UUID, name: String)

  case class CreateCategory(name: String)

  object CreateCategory {
    implicit val decoder: Decoder[CreateCategory] = deriveDecoder[CreateCategory]
    implicit val encoder: Encoder[CreateCategory] = deriveEncoder[CreateCategory]
  }

}
