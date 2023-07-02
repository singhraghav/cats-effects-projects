import cats.effect.{ ExitCode, IO, IOApp, Resource }
import com.comcast.ip4s.{ IpLiteralSyntax, Port }
import domain.conf.ApplicationConfig
import modules.{ HttpApi, Services }
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object main extends IOApp.Simple {

  implicit private val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  private def server(port: Int)(implicit services: Services[IO]): Resource[IO, Server] = {
    def initialize(port: Port): Resource[IO, Server] =
      EmberServerBuilder
        .default[IO]
        .withPort(port)
        .withHttpApp(HttpApi.make.httpApp)
        .build

    Port.fromInt(port) match {
      case Some(port) => initialize(port)
      case None       => initialize(port"8080")
    }
  }

  override def run: IO[Unit] =
    ApplicationConfig.load.flatMap { conf =>
      logger.info(s"Application Conf loaded $conf") *>
        conf.db.toHikariTransactorResource.flatMap { hikari =>
          server(conf.server.port)(Services(hikari))
        }.use(_ => IO.never.as(ExitCode.Success))
    }

}
