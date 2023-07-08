package domain

import cats.effect.IO
import io.circe.generic.JsonCodec
import io.circe.{ Decoder, Encoder }
import org.http4s.{ EntityDecoder, EntityEncoder, QueryParamDecoder }
import org.http4s.circe.{ jsonEncoderOf, jsonOf }

import java.util.UUID

object jsonc {

  implicit val uuidParamDecoder: QueryParamDecoder[UUID] = QueryParamDecoder[String].map(UUID.fromString)

  implicit def entityDecoder[A: Decoder]: EntityDecoder[IO, A] = jsonOf[IO, A]

  implicit def entityEncoder[A: Encoder]: EntityEncoder[IO, A] = jsonEncoderOf[A]

}

@JsonCodec
case class ResourceCreated(id: UUID)
