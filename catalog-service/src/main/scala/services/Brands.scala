package services

import cats.effect.IO
import cats.effect.kernel.Resource
import domain.brand.Brand
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.postgres.implicits._
import utils.doobieUtils._

import java.util.UUID

trait Brands[F[_]] {

  def create(name: String): F[UUID]

  def exists(name: String): F[Option[UUID]]

  def findAll: F[List[Brand]]
}

object Brands {

  def make(implicit postgres: Resource[IO, HikariTransactor[IO]]): Brands[IO] =
    new Brands[IO] {
      import BrandSQL._
      override def create(name: String): IO[UUID] = insertBrand(name).execute[IO]

      override def findAll: IO[List[Brand]] = selectAllBrands.execute[IO]

      override def exists(name: String): IO[Option[UUID]] = checkIfExists(name).execute[IO]
    }
}

private object BrandSQL {

  def insertBrand(name: String): doobie.ConnectionIO[UUID] =
    sql"INSERT INTO brands (id, name) VALUES (${UUID.randomUUID()}, $name)"
      .update.withUniqueGeneratedKeys[UUID]("id")

  val selectAllBrands: doobie.ConnectionIO[List[Brand]] =
    sql"SELECT * FROM brands".query[Brand].stream.compile.toList

  def checkIfExists(name: String): doobie.ConnectionIO[Option[UUID]] =
    sql"SELECT id FROM brands WHERE LOWER(name) = LOWER($name)".query[UUID].option

}
