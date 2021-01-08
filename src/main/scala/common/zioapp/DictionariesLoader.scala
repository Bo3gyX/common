package common.zioapp

import common.zioapp.Dictionaries.{Data, DefaultData}
import common.zioapp.DictionariesLoader.ServiceImpl.{CountriesResourceName, GenderResourceName}
import common.zioapp.FileLoader.ResourceFileLoader
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.{Decoder, Error}
import zio._

object DictionariesLoader {

  type DictionariesLoader = Has[DictionariesLoader.Service]

  trait Service {
    def load: Task[Dictionaries.Service]
    def get[T <: Data: Decoder](resourceName: String): Task[Map[String, T]]
  }

  val live: ULayer[DictionariesLoader] = ZLayer.succeed(new ServiceImpl)

  class ServiceImpl extends Service {

    override def load: Task[Dictionaries.Service] = {
      for {
        countries <- get[DefaultData](CountriesResourceName)
        gender    <- get[DefaultData](GenderResourceName)
      } yield {
        new Dictionaries.ServiceImpl(countries, gender)
      }
    }

    override def get[T <: Data: Decoder](resourceName: String): Task[Map[String, T]] = {
      for {
        json <- loadFromResource(resourceName)
        row  <- parse[T](json)
      } yield toMap[T](row)
    }

    private def loadFromResource(resourceName: String): Task[String] = {
      ResourceFileLoader.load(resourceName)
    }

    private def parse[T <: Data: Decoder](jsonString: String): IO[Error, Seq[T]] = {
      ZIO.fromEither(decode[Seq[T]](jsonString))
    }

    private def toMap[T <: Data](rows: Seq[T]): Map[String, T] = {
      rows.map(r => r.text -> r).toMap
    }
  }

  object ServiceImpl {
    val CountriesResourceName = "countries.json"
    val GenderResourceName    = "gender.json"
  }

}
