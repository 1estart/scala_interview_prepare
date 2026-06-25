import cats.MonadThrow
import cats.syntax.all._

// domain models
case class UserId(value: String)
case class Coordinates(lat: Double, lon: Double)
case class UserStatus(speedKmh: Double, isMoving: Boolean)

// prod: IO ( casssandra4io, fs2-kafka, ..)
// test: StateT, Id
trait UserRepository[F[_]] {
  def getLastCoordinates(userId: UserId): F[Option[Coordinates]]
  def saveStatus(userId: UserId, status: UserStatus): F[Unit]
}

trait RealTimeMessenger[F[_]] {
  def notifyFriend(friendId: UserId, userId: UserId, status: UserStatus): F[Unit]
}

// MonadThrow has raiseError/handleErrorWith
class FriendLocationService[F[_]: MonadThrow](
    userRepo: UserRepository[F],
    messenger: RealTimeMessenger[F]
) {
  
  def processLocationUpdate(
      userId: UserId, 
      newCoords: Coordinates, 
      friendIds: List[UserId]
  ): F[Unit] = {
    for {
      oldCoordsOpt <- userRepo.getLastCoordinates(userId)
      
      speed = calculateSpeed(oldCoordsOpt, newCoords)
      status = UserStatus(speedKmh = speed, isMoving = speed > 5.0)
      
      _ <- userRepo.saveStatus(userId, status)
      
      _ <- friendIds.traverse_(friendId => 
        messenger.notifyFriend(friendId, userId, status)
      )
    } yield ()
  }

  private def calculateSpeed(old: Option[Coordinates], current: Coordinates): Double = {
    old.map(_ => 62.0).getOrElse(0.0) 
  }
}

object ProductionApp {
  def makeService(repo: UserRepository[cats.effect.IO], msg: RealTimeMessenger[cats.effect.IO]) = 
    new FriendLocationService[cats.effect.IO](repo, msg)
}