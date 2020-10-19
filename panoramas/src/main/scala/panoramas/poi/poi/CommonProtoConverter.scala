package panoramas.poi.poi

import com.google.protobuf.Message
import panoramas.poi.Converter
import panoramas.poi.Converter._
import panoramas.poi.poi.Poi.{Coordinate, Point, Property}
import protobuf.panorama.PanoramaModels

trait CommonProtoConverter {

  type MessageConverter[M <: Message, T] = Converter[M, T]

  implicit val cnvCoordinate: MessageConverter[PanoramaModels.RelativeCoordinate, Coordinate] =
    new MessageConverter[PanoramaModels.RelativeCoordinate, Poi.Coordinate] {

      override def writes(obj: Poi.Coordinate): PanoramaModels.RelativeCoordinate = {
        PanoramaModels.RelativeCoordinate.newBuilder().setX(obj.x).setY(obj.y).build()
      }

      override def reads(obj: PanoramaModels.RelativeCoordinate): Poi.Coordinate = {
        Poi.Coordinate(obj.getX, obj.getY)
      }
    }

  implicit val cnvTextProperty: MessageConverter[PanoramaModels.TextProperty, Property.Text] =
    new MessageConverter[PanoramaModels.TextProperty, Property.Text] {

      override def writes(obj: Property.Text): PanoramaModels.TextProperty = {
        PanoramaModels.TextProperty.newBuilder().setText(obj.text).build()
      }

      override def reads(obj: PanoramaModels.TextProperty): Property.Text = {
        Property.Text(obj.getText)
      }
    }

  implicit val cnvImageProperty: MessageConverter[PanoramaModels.ImageProperty, Property.Image] =
    new MessageConverter[PanoramaModels.ImageProperty, Property.Image] {

      override def writes(obj: Property.Image): PanoramaModels.ImageProperty = {
        PanoramaModels.ImageProperty.newBuilder().setLink(obj.link).build()
      }

      override def reads(obj: PanoramaModels.ImageProperty): Property.Image = {
        Property.Image(obj.getLink)
      }
    }

  implicit val cnvVideoProperty: MessageConverter[PanoramaModels.VideoProperty, Property.Video] =
    new MessageConverter[PanoramaModels.VideoProperty, Property.Video] {

      override def writes(obj: Property.Video): PanoramaModels.VideoProperty = {
        PanoramaModels.VideoProperty.newBuilder().setLink(obj.link).build()
      }

      override def reads(obj: PanoramaModels.VideoProperty): Property.Video = {
        Property.Video(obj.getLink)
      }
    }

  implicit val cnvPoint: MessageConverter[PanoramaModels.Point, Point] =
    new MessageConverter[PanoramaModels.Point, Point] {

      override def writes(obj: Point): PanoramaModels.Point = {
        PanoramaModels.Point.newBuilder().setId(obj.id).setName(obj.name).setCoordinate(obj.coordinate).build()
      }

      override def reads(obj: PanoramaModels.Point): Point = {
        Point(obj.getId, obj.getName, obj.getCoordinate)
      }
    }
}
