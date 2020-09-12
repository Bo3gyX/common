package panoramas.poi.poi

import panoramas.poi.Converter._
import protobuf.panorama.PanoramaModels

trait ExteriorProtoConverter extends CommonProtoConverter {

  implicit val cnvExteriorProperties: MessageConverter[PanoramaModels.ExteriorProperty, ExteriorPoi.Properties] =
    new MessageConverter[PanoramaModels.ExteriorProperty, ExteriorPoi.Properties] {

      override def writes(obj: ExteriorPoi.Properties): PanoramaModels.ExteriorProperty = {
        PanoramaModels.ExteriorProperty
          .newBuilder()
          .setText(obj.text)
          .setYoutube(obj.youtube)
          .addAllImage(obj.images)
          .build()
      }

      override def reads(obj: PanoramaModels.ExteriorProperty): ExteriorPoi.Properties = {
        ExteriorPoi.Properties(obj.getText, obj.getYoutube, obj.getImageList)
      }
    }

  implicit val cnvExteriorPoi: MessageConverter[PanoramaModels.ExteriorPoi, ExteriorPoi] =
    new MessageConverter[PanoramaModels.ExteriorPoi, ExteriorPoi] {

      override def writes(obj: ExteriorPoi): PanoramaModels.ExteriorPoi = {
        val poi = PanoramaModels.Poi
          .newBuilder()
          .setId(obj.id)
          .setName(obj.name)
          .setCoordinate(obj.coordinate)
          .build()

        PanoramaModels.ExteriorPoi
          .newBuilder()
          .setPoi(poi)
          .setProperties(obj.properties)
          .build()
      }

      override def reads(obj: PanoramaModels.ExteriorPoi): ExteriorPoi = {
        ExteriorPoi(obj.getPoi.getId, obj.getPoi.getName, obj.getPoi.getCoordinate, obj.getProperties)
      }
    }
}
