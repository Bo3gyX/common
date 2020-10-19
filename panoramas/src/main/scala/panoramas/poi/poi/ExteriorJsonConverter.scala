package panoramas.poi.poi

import play.api.libs.json.{Format, Json}

trait ExteriorJsonConverter extends CommonJsonConverter {
  implicit val formatExteriorPoiProperties: Format[ExteriorPoi.Properties] = Json.format[ExteriorPoi.Properties]
  implicit val formatExteriorPoi: Format[ExteriorPoi]                      = Json.format[ExteriorPoi]
}
