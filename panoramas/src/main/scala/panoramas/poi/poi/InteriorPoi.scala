package panoramas.poi.poi

import panoramas.poi.poi.InteriorPoi.Properties
import panoramas.poi.poi.Poi.Coordinate
import panoramas.poi.poi.Poi.Property.{Image, Text}

case class InteriorPoi(id: String, name: String, coordinate: Coordinate, properties: Properties) extends Poi

object InteriorPoi {
  case class Properties(text: Text, images: Seq[Image])
}
