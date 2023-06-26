package services

import domain.item.Item

trait Items[F[_]] {

  def create(item: Item): F[String]

}
