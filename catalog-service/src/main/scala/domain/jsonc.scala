package domain

import cats.effect.IO
import io.circe
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.deriveDecoder
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

object jsonc {

  implicit def entityDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[IO, A] = jsonOf[IO, A]

  implicit def entityEncoder[A](implicit encoder: Encoder[A]): EntityEncoder[IO, A] = jsonEncoderOf[A]

}
