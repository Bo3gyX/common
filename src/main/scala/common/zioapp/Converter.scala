package common.zioapp

import common.zioapp.Dictionaries.Dictionaries
import zio.{Has, ZLayer}

class Converter(dictionaries: Dictionaries.Service) {

  def test: Unit = {
    val hid = dictionaries
      .countryByValue("Россия")
      .map(_.hid)
      .getOrElse(throw new Exception("Россия не найдена"))
    println(s"код Россия: $hid")
  }

}

object Converter {

  type CNV = Has[Converter]

  val live: ZLayer[Dictionaries, Nothing, CNV] = ZLayer.fromService { dic =>
    new Converter(dic)
  }

}
