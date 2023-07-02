package utils

import cats.effect.kernel.{MonadCancelThrow, Resource}
import doobie.hikari.HikariTransactor
import doobie.implicits._

object doobieUtils {
  implicit class ConnectionIOOps[A](action: doobie.ConnectionIO[A]) {
    def execute[F[_]: MonadCancelThrow](implicit res: Resource[F, HikariTransactor[F]]): F[A] =
      res.use(transactor => action.transact(transactor))
  }

}
