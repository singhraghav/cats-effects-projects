package services

import cats.effect.IO
import domain.item.{
  CreateItem,
  Item,
  ItemSearchQueryParam
}
import doobie.ConnectionIO
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.postgres.implicits._
import utils.doobieUtils._

import java.util.UUID

trait Items[F[_]] {

  def create(item: CreateItem): F[UUID]

  def search(param: ItemSearchQueryParam): F[List[Item]]
}

object Items {

  def make(implicit postgres: HikariTransactor[IO]): Items[IO] =
    new Items[IO] {
      import ItemsSQL._
      override def create(createItem: CreateItem): IO[UUID] = insertItem(createItem.toItem).execute[IO]

      override def search(param: ItemSearchQueryParam): IO[List[Item]] = param match {
        case ItemSearchQueryParam(Some(name), None, None)            => searchByName(name).execute[IO]
        case ItemSearchQueryParam(None, Some(brandId), None)         => searchByBrand(brandId).execute[IO]
        case ItemSearchQueryParam(Some(name), Some(brandId), None)   => searchByNameAndBrand(name, brandId).execute[IO]
        case ItemSearchQueryParam(None, Some(brandId), Some(shopId)) => searchByBrandAndShop(brandId, shopId).execute[IO]
        case ItemSearchQueryParam(None, None, Some(shopId))          => searchByShop(shopId).execute[IO]
        case _                                                       => IO(Nil)
      }
    }

}

private object ItemsSQL {

  def insertItem(item: Item): ConnectionIO[UUID] = {
    import item._
    sql"""
         INSERT INTO items (id, name, brand_id, quantity, shop_id)
         VALUES ($id, $name, $brandId, $quantity, $shopId)
       """
      .update
      .withUniqueGeneratedKeys[UUID]("id")
  }

  def searchByName(name: String): ConnectionIO[List[Item]] = {
    sql"""
         SELECT * FROM items
         WHERE name=$name
       """
      .query[Item]
      .accumulate[List]
  }

  def searchByBrand(brandId: UUID): ConnectionIO[List[Item]] = {
    sql"""
           SELECT * FROM items
           WHERE brand_id=$brandId
         """
      .query[Item]
      .accumulate[List]
  }

  def searchByNameAndBrand(name: String, brandId: UUID): ConnectionIO[List[Item]] = {
    sql"""
           SELECT * FROM items
           WHERE name=$name AND brand_id=$brandId
         """
      .query[Item]
      .accumulate[List]
  }

  def searchByShop(shopId: UUID): ConnectionIO[List[Item]] = {
    sql"""
           SELECT * FROM items
           WHERE shop_id=$shopId
         """
      .query[Item]
      .accumulate[List]
  }

  def searchByBrandAndShop(brandId: UUID, shopId: UUID): ConnectionIO[List[Item]] = {
    sql"""
           SELECT * FROM items
           WHERE brand_id=$brandId AND shop_id=$shopId
         """
      .query[Item]
      .accumulate[List]
  }

}
