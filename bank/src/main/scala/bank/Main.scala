package bank

import util.zio.ZioRunner
import zio.{Task, ZIO}

object Main extends ZioRunner {

  override def start: ZIO[Main.AppEnv, Any, Any] = {
    Task(LogParser.run)
  }
}
