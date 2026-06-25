import cats.effect.*
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s.*
import sttp.tapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.*
import sttp.tapir.server.http4s.Http4sServerInterpreter
import io.circe.generic.auto.*

case class Tour(id: String, hotel: String, price: Double)

val tourEndpoint: PublicEndpoint[String, Unit, Tour, Any] =
  endpoint.get
    .in("tours" / path[String]("id"))
    .out(jsonBody[Tour])

val tourRoute = tourEndpoint.serverLogic { tourId =>
  IO.pure(Right(Tour(tourId, "Hotel California", 100.0)))
}

object Main extends IOApp.Simple:
  def run: IO[Unit] =
    EmberServerBuilder
      .default[IO]
      .withHost(host"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(
        Http4sServerInterpreter[IO]()
          .toRoutes(tourRoute)
          .orNotFound
      )
      .build
      .useForever