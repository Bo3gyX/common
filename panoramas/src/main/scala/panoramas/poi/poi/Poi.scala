package panoramas.poi.poi

import panoramas.poi.poi.Poi.Coordinate

trait Poi {
  def id: String
  def name: String
  def coordinate: Coordinate
}

object Poi {
  case class Coordinate(x: Double, y: Double)

  sealed trait Property

  object Property {
    case class Text(text: String)  extends Property
    case class Image(link: String) extends Property
    case class Video(link: String) extends Property
  }

}
