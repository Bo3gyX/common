package common

import util.zio.ZioRunner
import zio.ZIO
import zio.clock.Clock

object ZioApp extends ZioRunner {

  override def start: ZIO[AppEnv, Any, Any] = {
    for {
      clock    <- ZIO.service[Clock.Service]
      instant1 <- clock.instant
      instant2 <- clock.instant
      instant3 <- clock.instant
    } yield {
      println(instant1)
      println(instant2)
      println(instant3)
    }
  }

}
