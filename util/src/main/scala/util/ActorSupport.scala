package util

import akka.actor.{ActorSystem, Scheduler}

import scala.concurrent.ExecutionContext

trait ActorSupport {

  val actorSystemName: String

  implicit lazy val actorSystem: ActorSystem = ActorSystem(actorSystemName)

  implicit lazy val executionContext: ExecutionContext = actorSystem.dispatcher

  lazy val scheduler: Scheduler = actorSystem.scheduler
}
