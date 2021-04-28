package common.zioapp

import akka.actor.ActorSystem
import zio._
import zio.logging.{log, Logging}

object AkkaActorSystem {

  type AkkaActorSystem = Has[AkkaActorSystem.Service]

  trait Service {
    def actorSystem: ActorSystem
  }

  object Service {

    def apply(name: String): Service = new Service {
      override def actorSystem: ActorSystem = ActorSystem(name)
    }
  }

  def Live(name: String): ZLayer[Logging, Nothing, AkkaActorSystem] = ZLayer.fromManaged {
    def acquire = {
      for {
        _       <- log.info(s"initializing ActorSystem $name")
        service <- UIO(Service(name))
        _       <- log.info(s"initialized ActorSystem $name")
      } yield service
    }

    def release(service: Service) = {
      for {
        _          <- log.info(s"stopping ActorSystem $name")
        terminated <- ZIO.fromFuture(_ => service.actorSystem.terminate()).orDie
        _          <- log.info(s"stopped ActorSystem $name")
      } yield terminated
    }

    ZManaged.make(acquire)(release)
  }
}
