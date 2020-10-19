package panoramas.poi.poi

import panoramas.poi.Converter._
import protobuf.panorama.PanoramaModels

trait InteriorProtoConverter extends CommonProtoConverter {

  implicit val cnvInteriorProperties: MessageConverter[PanoramaModels.InteriorProperty, InteriorPoi.Properties] =
    new MessageConverter[PanoramaModels.InteriorProperty, InteriorPoi.Properties] {

      override def writes(obj: InteriorPoi.Properties): PanoramaModels.InteriorProperty = {
        PanoramaModels.InteriorProperty
          .newBuilder()
          .setText(obj.text)
          .addAllImage(obj.images)
          .build()
      }

      override def reads(obj: PanoramaModels.InteriorProperty): InteriorPoi.Properties = {
        InteriorPoi.Properties(obj.getText, obj.getImageList)
      }
    }

  implicit val cnvInteriorPoi: MessageConverter[PanoramaModels.InteriorPoi, InteriorPoi] =
    new MessageConverter[PanoramaModels.InteriorPoi, InteriorPoi] {

      override def writes(obj: InteriorPoi): PanoramaModels.InteriorPoi = {
        PanoramaModels.InteriorPoi
          .newBuilder()
          .setPoint(obj.point)
          .setProperties(obj.properties)
          .build()
      }

      override def reads(obj: PanoramaModels.InteriorPoi): InteriorPoi = {
        InteriorPoi(obj.getPoint, obj.getProperties)
      }
    }
}
