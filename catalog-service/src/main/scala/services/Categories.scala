package services

import cats.effect.IO
import domain.category.Category
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.postgres.implicits._
import utils.doobieUtils._

import java.util.UUID

trait Categories[F[_]] {

  def create(name: String): F[UUID]

  def findByName(name: String): F[List[Category]]

  def findById(id: UUID): F[List[Category]]

  def findAll: F[List[Category]]

}

object Categories {

  def make(implicit postgres: HikariTransactor[IO]): Categories[IO] =
    new Categories[IO] {
      import CategoriesSQL._
      override def create(name: String): IO[UUID] = insertCategory(name).execute[IO]

      override def findByName(name: String): IO[List[Category]] = getByName(name).execute[IO]

      override def findById(id: UUID): IO[List[Category]] = getById(id).execute[IO]

      override def findAll: IO[List[Category]] = selectAllBrands.execute[IO]
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

  def getByName(name: String): doobie.ConnectionIO[List[Category]] =
    sql"SELECT * FROM categories WHERE LOWER(name) = LOWER($name)".query[Category].accumulate[List]

  def getById(id: UUID): doobie.ConnectionIO[List[Category]] =
    sql"SELECT * FROM categories WHERE id = $id".query[Category].accumulate[List]

}
