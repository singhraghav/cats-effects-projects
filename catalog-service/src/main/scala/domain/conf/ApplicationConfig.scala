package domain.conf

import cats.effect.{IO, Resource}
import doobie.hikari.HikariTransactor
import pureconfig.ConfigConvert.fromReaderAndWriter
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax.CatsEffectConfigSource

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

case class ApplicationConfig(server: HttpServerConfig, db: DbConfig)

object ApplicationConfig {
  def load: IO[ApplicationConfig] = ConfigSource.default.loadF[IO, ApplicationConfig]
}

case class HttpServerConfig(port: Int)

case class DbConfig(driver: String, url: String, user: String, password: String, connectionPoolSize: Int) {
  private def executionContext: Resource[IO, ExecutionContext] = {
    Resource
      .make(IO.delay(ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(connectionPoolSize)))) { ec =>
        IO(ec.shutdown())
      }
  }

  def toHikariTransactorResource: Resource[IO, HikariTransactor[IO]] =
    executionContext
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
