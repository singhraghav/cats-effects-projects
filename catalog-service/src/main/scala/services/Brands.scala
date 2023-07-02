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

  def findByName(name: String): F[List[Brand]]

  def findById(id: UUID): F[List[Brand]]

  def findAll: F[List[Brand]]
}

object Brands {

  def make(implicit postgres: HikariTransactor[IO]): Brands[IO] =
    new Brands[IO] {
      import BrandSQL._

      override def create(name: String): IO[UUID] = insertBrand(name.toLowerCase).execute[IO]

      override def findAll: IO[List[Brand]] = selectAllBrands.execute[IO]

      override def findByName(name: String): IO[List[Brand]] = getByName(name).execute[IO]

      override def findById(id: UUID): IO[List[Brand]] = getById(id).execute[IO]
    }
}

private object BrandSQL {

  def insertBrand(name: String): doobie.ConnectionIO[UUID] = {
    sql"""
        WITH e AS(
          INSERT INTO brands (id, name)
          VALUES (${UUID.randomUUID()}, $name)
          ON CONFLICT(name) DO NOTHING
          RETURNING id
        )
        SELECT id FROM e
        UNION
        SELECT id FROM brands WHERE name=$name
       """
      .query[UUID]
      .unique
  }

  val selectAllBrands: doobie.ConnectionIO[List[Brand]] =
    sql"SELECT * FROM brands".query[Brand].stream.compile.toList

  def getByName(name: String): doobie.ConnectionIO[List[Brand]] =
    sql"SELECT * FROM brands WHERE LOWER(name) = LOWER($name)".query[Brand].accumulate[List]

  def getById(id: UUID): doobie.ConnectionIO[List[Brand]] =
    sql"SELECT * FROM brands WHERE id = $id".query[Brand].accumulate[List]

}
