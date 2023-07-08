package services

import cats.effect.IO
import domain.user.{ CreateUser, User }
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.postgres.implicits._
import utils.doobieUtils._

import java.util.UUID
trait Users[F[_]] {

  def create(userDetails: CreateUser): F[UUID]

  def findByEmail(email: String): F[Option[User]]

  def findById(id: UUID): F[Option[User]]

}

object Users {

  def make(implicit postgres: HikariTransactor[IO]): Users[IO] = {
    new Users[IO] {
      import UserSQL._

      override def create(userDetails: CreateUser): IO[UUID] =
        insertUser(userDetails).execute[IO]

      override def findByEmail(email: String): IO[Option[User]] = getByEmail(email).execute[IO]

      override def findById(id: UUID): IO[Option[User]] = getById(id).execute[IO]
    }
  }
}

private object UserSQL {

  def insertUser(createUser: CreateUser): doobie.ConnectionIO[UUID] = {
    import createUser._
    sql"""
         INSERT INTO users (id, first_name, last_name, user_type, email)
         VALUES (${UUID.randomUUID()}, $firstName, $lastName, $userType, $email)
       """
      .update
      .withUniqueGeneratedKeys[UUID]("id")
  }

  def getByEmail(email: String): doobie.ConnectionIO[Option[User]] = {
    sql"""
         SELECT * FROM users
         WHERE email=$email
       """
      .query[User]
      .option
  }

  def getById(id: UUID): doobie.ConnectionIO[Option[User]] = {
    sql"""
         SELECT * FROM users
         WHERE id=$id
       """
      .query[User]
      .option
  }
}
