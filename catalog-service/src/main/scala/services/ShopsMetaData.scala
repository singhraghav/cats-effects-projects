package services

import cats.effect.IO
import domain.shop.{ CreateShop, ShopMetaData }
import doobie.ConnectionIO
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.postgres.implicits._
import utils.doobieUtils._

import java.util.UUID

trait ShopsMetaData[F[_]] {

  def create(shopDetails: CreateShop): F[UUID]

  def findByName(name: String): F[Option[ShopMetaData]]

  def findById(id: UUID): F[Option[ShopMetaData]]

  def getAll: F[List[ShopMetaData]]

  def findByOwner(ownerId: UUID): F[List[ShopMetaData]]

  def removeShop(shopId: UUID): F[Int]

}

object ShopsMetaData {

  def make(implicit postgres: HikariTransactor[IO]): ShopsMetaData[IO] = {

    new ShopsMetaData[IO] {
      import ShopsMetaDataSQL._

      override def create(shopDetails: CreateShop): IO[UUID] = insertShop(shopDetails.toShopMetaData).execute[IO]

      override def findByName(name: String): IO[Option[ShopMetaData]] = getByName(name.toLowerCase()).execute[IO]

      override def findById(shopId: UUID): IO[Option[ShopMetaData]] = getById(shopId).execute[IO]

      override def getAll: IO[List[ShopMetaData]] = allData().execute[IO]

      override def findByOwner(ownerId: UUID): IO[List[ShopMetaData]] = getByOwner(ownerId).execute[IO]

      override def removeShop(shopId: UUID): IO[Int] = deleteShop(shopId).execute[IO]
    }
  }
}

private object ShopsMetaDataSQL {

  def insertShop(shopDetails: ShopMetaData): ConnectionIO[UUID] = {
    import shopDetails._
    sql"""INSERT INTO shop_meta_data (id, name, owner_id) VALUES ($id, $name, $owner)"""
      .update
      .withUniqueGeneratedKeys[UUID]("id")
  }

  def getByName(name: String): ConnectionIO[Option[ShopMetaData]] = {
    sql"""SELECT * FROM shop_meta_data WHERE name=$name"""
      .query[ShopMetaData]
      .option
  }

  def getById(shopId: UUID): ConnectionIO[Option[ShopMetaData]] = {
    sql"""SELECT * FROM shop_meta_data WHERE id=$shopId"""
      .query[ShopMetaData]
      .option
  }

  def getByOwner(ownerId: UUID): ConnectionIO[List[ShopMetaData]] = {
    sql"""SELECT * FROM shop_meta_data WHERE owner_id=$ownerId"""
      .query[ShopMetaData]
      .accumulate[List]
  }

  def allData(): ConnectionIO[List[ShopMetaData]] = {
    sql"""SELECT * FROM shop_meta_data"""
      .query[ShopMetaData]
      .accumulate[List]
  }

  def deleteShop(shopId: UUID): ConnectionIO[Int] = {
    sql"""DELETE FROM shop_meta_data where id=$shopId"""
      .update
      .run
  }

}
