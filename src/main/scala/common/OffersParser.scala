package common

import play.api.libs.json.{JsValue, Json}

import scala.collection.mutable
import scala.io.Source

object OffersParser {

  def start(): Unit = {

    val filePath = "/Users/ridrisov/work/autoru-api/offers.json"
    val source   = Source.fromFile(filePath)
    val str      = source.mkString
    source.close()

    val json     = Json.parse(str)
    val jsOffers = (json \ "offers").as[Array[JsValue]]

    println(s"offers size: ${jsOffers.length}")

    val ids = mutable.Map.empty[String, Int].withDefaultValue(0)

    for ((jsOffer, idx) <- jsOffers.zipWithIndex) {
      val id         = (jsOffer \ "id").as[String]
      val panoramaId = (jsOffer \ "state" \ "external_panorama" \ "published" \ "id").asOpt[String]

      val count = panoramaId.map { id =>
        val c = ids(id)
        val n = c + 1
        ids.update(id, n)
        n
      }

      println(s"$idx: offer: $id; panorama: $panoramaId -> ${count.getOrElse("???")}")
    }

    println(s"known ids: ${ids.keySet.size}")
  }

}
