package panoramas.poi.poi

import panoramas.poi.poi.InteriorPoi.Properties
import panoramas.poi.poi.Poi.Point
import panoramas.poi.poi.Poi.Property.{Image, Text}

case class InteriorPoi(point: Point, properties: Properties) extends Poi

object InteriorPoi {
  case class Properties(text: Text, images: Seq[Image])
}
