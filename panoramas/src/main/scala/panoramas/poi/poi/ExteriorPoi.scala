package panoramas.poi.poi

import panoramas.poi.poi.ExteriorPoi.Properties
import panoramas.poi.poi.Poi.Point
import panoramas.poi.poi.Poi.Property.{Image, Text, Video}

case class ExteriorPoi(point: Point, properties: Properties) extends Poi

object ExteriorPoi {
  case class Properties(text: Text, youtube: Video, images: Seq[Image])
}
