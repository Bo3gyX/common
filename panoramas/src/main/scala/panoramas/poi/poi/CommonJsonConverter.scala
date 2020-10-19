package panoramas.poi.poi

import panoramas.poi.Converter
import panoramas.poi.poi.Poi.{Coordinate, Point, Property}
import play.api.libs.json._

trait CommonJsonConverter {

  type JsonConverter[T] = Converter[JsValue, T]

  implicit def jsonConverter[T: Format]: JsonConverter[T] = new JsonConverter[T] {
    private val format: Format[T]        = implicitly[Format[T]]
    override def writes(obj: T): JsValue = format.writes(obj)

    override def reads(obj: JsValue): T = format.reads(obj) match {
      case JsSuccess(value, _) => value
      case JsError(errors)     => throw JsResultException(errors)
    }
  }

  implicit val formatCoordinate: Format[Coordinate]        = Json.format[Coordinate]
  implicit val formatPropertyText: Format[Property.Text]   = Json.format[Property.Text]
  implicit val formatPropertyImage: Format[Property.Image] = Json.format[Property.Image]
  implicit val formatPropertyVideo: Format[Property.Video] = Json.format[Property.Video]
  implicit val formatPoint: Format[Point]                  = Json.format[Point]

}
