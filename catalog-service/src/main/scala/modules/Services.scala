package modules

import cats.effect.IO
import doobie.hikari.HikariTransactor
import services.{Brands, Categories}

case class Services[F[_]](brands: Brands[F], categories: Categories[F])

object Services {
  def apply(postgres: HikariTransactor[IO]): Services[IO] =
    new Services[IO](Brands.make(postgres), Categories.make(postgres))

}
