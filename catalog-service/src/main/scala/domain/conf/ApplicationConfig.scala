package domain.conf

import cats.effect.{ IO, Resource }
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import pureconfig.ConfigConvert.fromReaderAndWriter
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax.CatsEffectConfigSource

case class ApplicationConfig(server: HttpServerConfig, db: DbConfig)

object ApplicationConfig {
  def load: IO[ApplicationConfig] = ConfigSource.default.loadF[IO, ApplicationConfig]
}

case class HttpServerConfig(port: Int)

case class DbConfig(driver: String, url: String, user: String, password: String, connectionPoolSize: Int) {

  def toHikariTransactorResource: Resource[IO, HikariTransactor[IO]] =
    ExecutionContexts.fixedThreadPool[IO](connectionPoolSize)
      .flatMap { ec =>
        HikariTransactor.newHikariTransactor[IO](
          driverClassName = driver,
          url = url,
          user = user,
          pass = password,
          connectEC = ec
        )
      }
}
