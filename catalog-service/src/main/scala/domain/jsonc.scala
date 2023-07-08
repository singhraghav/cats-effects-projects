package domain

import cats.effect.IO
import io.circe.{ Decoder, Encoder }
import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }
import org.http4s.{ EntityDecoder, EntityEncoder, QueryParamDecoder }
import org.http4s.circe.{ jsonEncoderOf, jsonOf }

import java.util.UUID

object jsonc {

  implicit val uuidParamDecoder: QueryParamDecoder[UUID] = QueryParamDecoder[String].map(UUID.fromString)

  implicit def entityDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[IO, A] = jsonOf[IO, A]

  implicit def entityEncoder[A](implicit encoder: Encoder[A]): EntityEncoder[IO, A] = jsonEncoderOf[A]

}

case class ResourceCreated(id: UUID)

object ResourceCreated {
  implicit val decoder: Decoder[ResourceCreated] = deriveDecoder[ResourceCreated]
  implicit val encoder: Encoder[ResourceCreated] = deriveEncoder[ResourceCreated]
}
