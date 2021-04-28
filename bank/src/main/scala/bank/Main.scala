package bank

import bank.gazprombank.Entities.{Offer, Owner}
import bank.gazprombank.OfferConverter
import util.zio.ZioRunner
import zio.ZIO

object Main extends ZioRunner {

  override def start: ZIO[Main.AppEnv, Any, Any] = {
    for {
      str <- test
    } yield {
      println(str)
    }
  }

  private def test = {
    val owner = Owner("Alex", Some("7926789011"))
    val offer = Offer("bmw", Some(100500), None)
    val valid = OfferConverter.convert(offer)
    ZIO.fromEither(valid.toEither)
  }

}
