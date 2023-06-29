import cats.effect.{ IO, IOApp, Resource }
import com.comcast.ip4s.IpLiteralSyntax
import modules.HttpApi
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.typelevel.log4cats.slf4j.Slf4jLogger

object main extends IOApp.Simple {

  implicit private val logger = Slf4jLogger.getLogger[IO]

  private val server: Resource[IO, Server] = {
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(HttpApi.make.httpApp)
      .build
  }

  override def run: IO[Unit] = server.use(_ => IO.never)

}
