package common

import util.zio.ZioRunner
import util.zio.ZioRunner.AppEnv
import zio.logging.{log, Logging}
import zio.{Task, ZIO}

object ZioApp extends ZioRunner {

  override def start: ZIO[AppEnv, Any, Any] = {
    process(State(0))
  }

  case class State(counter: Int)

  def process(state: State): ZIO[Logging, Throwable, State] = {
    (for {
      state <- Task.succeed(state.copy(counter = state.counter + 1))
      _     <- log.info(s"new State: $state")
    } yield state).flatMap(process)
  }

}
