package modules

import cats.effect.IO
import doobie.hikari.HikariTransactor
import services.{ Brands, Categories, Users }

case class Services[F[_]](users: Users[F], brands: Brands[F], categories: Categories[F])

object Services {
  def apply(postgres: HikariTransactor[IO]): Services[IO] =
    new Services[IO](Users.make(postgres), Brands.make(postgres), Categories.make(postgres))

}
