package services

import cats.effect.IO
import cats.effect.kernel.Resource
import domain.category.Category
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.postgres.implicits._
import utils.doobieUtils._

import java.util.UUID

trait Categories[F[_]] {

  def create(name: String): F[UUID]

  def exists(name: String): F[Option[UUID]]

  def findAll: F[List[Category]]

}

object Categories {

  def make(implicit postgres: HikariTransactor[IO]): Categories[IO] =
    new Categories[IO] {
      import CategoriesSQL._
      override def create(name: String): IO[UUID] = insertCategory(name).execute[IO]

      override def findAll: IO[List[Category]] = selectAllBrands.execute[IO]

      override def exists(name: String): IO[Option[UUID]] = checkIfExists(name).execute[IO]
    }
}

private object CategoriesSQL {

  def insertCategory(name: String): doobie.ConnectionIO[UUID] =
    sql"""
       WITH e AS(
         INSERT INTO categories (id, name)
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

  val selectAllBrands: doobie.ConnectionIO[List[Category]] =
    sql"SELECT * FROM categories".query[Category].stream.compile.toList

  def checkIfExists(name: String): doobie.ConnectionIO[Option[UUID]] =
    sql"SELECT id FROM categories WHERE LOWER(name) = LOWER($name)".query[UUID].option

}
