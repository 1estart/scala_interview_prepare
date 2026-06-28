import org.typelevel.doobie._
import org.typelevel.doobie.implicits._
import org.typelevel.doobie.hikari.HikariTransactor
import cats.effect.IO
import scala.concurrent.ExecutionContext

import cats.effect.unsafe.implicits.global

import cats.effect.Temporal
import cats.syntax.all.*
import fs2.Stream
import scala.concurrent.duration.*
import cats.effect.Resource

case class SearchRequest(id: Int, query: String, status: String)

def processPendingRequests(xa: Transactor[IO])(using t: Temporal[IO]): IO[Int] =
  sql"SELECT id, query, status FROM search_requests WHERE status = 'pending'"
    .query[SearchRequest]
    .stream
    .transact(xa)
    .parEvalMapUnordered(10) { req =>
      callExternalAPI(req.query).attempt
        .flatMap {
          case Right(_) =>
            sql"UPDATE search_requests SET status = 'completed' WHERE id = ${req.id}".update.run
              .transact(xa)
              .as(1)
          case Left(_) =>
            sql"UPDATE search_requests SET status = 'failed' WHERE id = ${req.id}".update.run
              .transact(xa)
              .as(0)
        }
    }
    .compile
    .foldMonoid

def callExternalAPI(query: String): IO[Unit] =
  Temporal[IO].sleep(100.millis)

def periodicSearchProcessor(xa: Transactor[IO]): Stream[IO, Unit] =
  Stream
    .awakeEvery[IO](30.seconds)
    .evalMap { _ =>
      processPendingRequests(xa).flatMap { count =>
        IO.println(s"Processed $count pending requests")
      }
    }

object Doobie {
  val xa = Transactor.fromDriverManager[IO](
    driver = "org.postgresql.Driver",
    url = "jdbc:postgresql:world",
    user = "postgres",
    password = "password",
    logHandler = None
  )

  def createTransactor: Resource[IO, Transactor[IO]] =
    HikariTransactor.newHikariTransactor[IO](
      driverClassName = "org.postgresql.Driver",
      url = "jdbc:postgresql://localhost:5432/world-db",
      user = "world",
      pass = "world123",
      connectEC = scala.concurrent.ExecutionContext.global
    )

  def runMigrations(xa: Transactor[IO]): IO[Unit] =
    sql"""
      CREATE TABLE IF NOT EXISTS search_requests (
        id SERIAL PRIMARY KEY,
        query VARCHAR(255) NOT NULL,
        status VARCHAR(50) NOT NULL DEFAULT 'pending',
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      )
    """.update.run.transact(xa).void

  def seedData(xa: Transactor[IO]): IO[Unit] =
    sql"""
      INSERT INTO search_requests (query, status) 
      SELECT 'Paris, France', 'pending'
      WHERE NOT EXISTS (SELECT 1 FROM search_requests WHERE query = 'Paris, France')
    """.update.run.transact(xa).void

}
