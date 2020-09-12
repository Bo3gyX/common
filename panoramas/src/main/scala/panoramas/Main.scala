package panoramas

import panoramas.poi.PoiProtoExample
import panoramas.poi.poi.{ExteriorProtoConverter, InteriorProtoConverter}
import util.Application
import panoramas.poi.Converter._

import scala.language.{higherKinds, implicitConversions}

object Main extends Application with ExteriorProtoConverter with InteriorProtoConverter {

  println(PoiProtoExample.exteriorPoi)
  println(read(PoiProtoExample.exteriorPoi))

  println(PoiProtoExample.interiorPoi)
  println(read(PoiProtoExample.interiorPoi))

}
