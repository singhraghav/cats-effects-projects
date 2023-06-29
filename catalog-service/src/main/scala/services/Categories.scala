package services

import domain.category.Category

trait Categories[F[_]] {

  def create(name: String): F[String]

  def findAll : F[List[Category]]

}
