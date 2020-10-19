package panoramas.poi.poi

import panoramas.poi.poi.Poi.Point

trait Poi {
  def point: Point
}

object Poi {
  case class Point(id: String, name: String, coordinate: Coordinate)

  case class Coordinate(x: Double, y: Double)

  sealed trait Property

  object Property {
    case class Text(text: String)  extends Property
    case class Image(link: String) extends Property
    case class Video(link: String) extends Property
  }

}
