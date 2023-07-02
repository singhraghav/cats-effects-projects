package domain

import io.circe.{Decoder, Encoder}

import java.util.UUID
import io.circe.generic.semiauto._


object brand {
  case class Brand(id: UUID, name: String)

  object Brand {
    def apply(name: String): Brand = new Brand(UUID.randomUUID(), name)
  }

  case class CreateBrand(name: String)

  object CreateBrand {
    implicit val createBandDecoder: Decoder[CreateBrand] = deriveDecoder[CreateBrand]
    implicit val createBandEncoder: Encoder[CreateBrand] = deriveEncoder[CreateBrand]
  }

}
