package modules

import cats.effect.IO
import doobie.hikari.HikariTransactor
import services.Brands

case class Services[F[_]](brands: Brands[F])

object Services {
  def apply(postgres: HikariTransactor[IO]): Services[IO] =
    new Services[IO](Brands.make(postgres))

}
