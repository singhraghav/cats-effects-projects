package db

import cats.effect.{ IO, IOApp }
import domain.brand.Brand
import domain.conf.ApplicationConfig
import doobie.util.transactor.Transactor
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.update.Update
import services.Brands

import java.util.UUID

object DoobieDemo extends IOApp.Simple {

  implicit class Debugger[A](io: IO[A]) {
    def myDebug: IO[A] = io.map { a =>
      println(s"[${Thread.currentThread().getName}] $a")
      a
    }
  }

  val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5432/catalog_service",
    "admin",
    "12345"
  )

  def findAllBrands(): IO[List[String]] = {
    val query  = sql"SELECT name FROM brands".query[String]
    val action = query.to[List]
    action.transact(xa)
  }

  def findBrandById(id: UUID): IO[Option[Brand]] = {
    val query  = sql"SELECT * FROM brands where id=$id".query[Brand]
    val action = query.option
    action.transact(xa)
  }

  def finAllBrandsStream: IO[List[Brand]] = sql"SELECT * FROM brands".query[Brand].stream.compile.toList.transact(xa)

  def insertNewBrand(name: String): IO[UUID] = {
    val query = sql"INSERT INTO brands (id, name) VALUES (${UUID.randomUUID()}, $name)"
    query.update.withUniqueGeneratedKeys[UUID]("id").transact(xa)
  }

  def insertManyBrands(brands: List[Brand]): IO[Int] = {
    val statement = "INSERT INTO brands (id, name) VALUES (?, ?)"
    val action    = Update[Brand](statement).updateManyWithGeneratedKeys[Brand]("id", "name")(brands)
    action.compile.toList.transact(xa).map(_.length)
  }
  override def run: IO[Unit] =
    for {
      appConf <- ApplicationConfig.load.myDebug
      _       <- Brands.make(appConf.db.toHikariTransactorResource).exists("lEviS").myDebug
    } yield ()
}
