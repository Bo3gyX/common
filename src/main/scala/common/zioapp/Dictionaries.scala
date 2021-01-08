package common.zioapp

import common.zioapp.DictionariesLoader.DictionariesLoader
import common.zioapp.DictionariesLoader.ServiceImpl.{CountriesResourceName, GenderResourceName}
import zio.{Has, ZLayer}

object Dictionaries {

  type Dictionaries = Has[Dictionaries.Service]

  trait Data {
    def hid: String
    def text: String
  }

  case class DefaultData(hid: String, text: String) extends Data

  trait Service {
    def countryByValue(value: String): Option[DefaultData]
    def genderByValue(value: String): Option[DefaultData]
  }

  val live: ZLayer[DictionariesLoader, Throwable, Dictionaries] = ZLayer.fromServiceM { loader =>
    import io.circe.generic.auto._
    for {
      countries <- loader.get[DefaultData](CountriesResourceName)
      gender    <- loader.get[DefaultData](GenderResourceName)
    } yield {
      new Dictionaries.ServiceImpl(countries, gender)
    }
  }

  class ServiceImpl(countries: Map[String, DefaultData], gender: Map[String, DefaultData]) extends Service {
    override def countryByValue(value: String): Option[DefaultData] = countries.get(value)
    override def genderByValue(value: String): Option[DefaultData]  = gender.get(value)

    override def toString: String = Seq(countries, gender).toString()
  }

}
