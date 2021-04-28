package common

import util.zio.ZioRunner
import zio._
import zio.clock.Clock

import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object ZioApp extends ZioRunner {

  def opt: Option[String] = throw new Exception("AAA")

  override def start = {
//    val eff = ZIO.unit
    val eff = for {
      clock <- ZIO.service[Clock.Service]
      t1 <- clock.localDateTime
      t2 <- clock.instant
    } yield {
      val t21 = t2.atZone(ZoneOffset.UTC)
      println(t1)
      println(t2)
      println(t21.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
    }

    eff.onError(err => URIO.succeed(println(err.prettyPrint)))
  }


}
