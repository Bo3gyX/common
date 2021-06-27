package common

import common.zioapp.MyProcessor.MyContext
import common.zioapp.Processor
import common.zioapp.Processor.ProcessorService
import util.zio.ZioRunner
import zio.ZIO

object ZioApp extends ZioRunner {

  override def start: ZIO[AppEnv, Any, Any] = {
    val program = for {
      processor <- ZIO.service[ProcessorService]
      _         <- processor.run(MyContext(0))
    } yield ()
    program.provideSomeLayer(Processor.live)
  }

}
