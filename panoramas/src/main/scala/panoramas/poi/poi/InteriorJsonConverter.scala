package panoramas.poi.poi

import play.api.libs.json.{Format, Json}

trait InteriorJsonConverter extends CommonJsonConverter {
  implicit val formatInteriorPoiProperties: Format[InteriorPoi.Properties] = Json.format[InteriorPoi.Properties]
  implicit val formatInteriorPoi: Format[InteriorPoi]                      = Json.format[InteriorPoi]
}
