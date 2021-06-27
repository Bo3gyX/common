package common

import util.zio.ZioRunner
import util.zio.ZioRunner.AppEnv
import zio.ZIO

object ZioApp extends ZioRunner {

  override def start: ZIO[AppEnv, Any, Any] = {
    ZIO.unit
  }

}
