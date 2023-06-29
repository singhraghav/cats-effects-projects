package services

import domain.brand.Brand

trait Brands[F[_]] {

  def create(name: String): F[String]

  def findAll: F[List[Brand]]
}
