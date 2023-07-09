package modules

import cats.effect.IO
import doobie.hikari.HikariTransactor
import services.{Brands, Categories, ShopsMetaData, Users}

case class Services[F[_]](users: Users[F], brands: Brands[F], categories: Categories[F], shops: ShopsMetaData[F])

object Services {
  def apply(implicit postgres: HikariTransactor[IO]): Services[IO] =
    new Services[IO](Users.make, Brands.make, Categories.make, ShopsMetaData.make)

}
