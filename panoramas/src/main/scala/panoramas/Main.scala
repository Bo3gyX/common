package panoramas

import panoramas.Main.Proto.poi1
import panoramas.poi.poi._
import panoramas.poi.{Converter, PoiProtoExample}
import play.api.libs.json.JsValue
import util.Application

import scala.language.{higherKinds, implicitConversions}

object Main extends Application {

  object Proto extends ExteriorProtoConverter with InteriorProtoConverter {
    val poi1: InteriorPoi = Converter.read(PoiProtoExample.interiorPoi)

    val poi2: ExteriorPoi = Converter.read(PoiProtoExample.exteriorPoi)
    println(poi2)
  }

  object Json extends ExteriorJsonConverter with InteriorJsonConverter {
    val json: JsValue = Converter.write(poi1)
  }

  println(Proto.poi1)
  println(Proto.poi2)
  println(Json.json)
}
