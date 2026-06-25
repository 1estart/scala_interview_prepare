import cats.effect.*
import cats.implicits.*

object Main extends IOApp.Simple {
  def run: IO[Unit] = 
    IO.println("Tour Search Engine starting...")
}