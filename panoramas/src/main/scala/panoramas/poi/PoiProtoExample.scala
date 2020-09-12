package panoramas.poi

import protobuf.panorama.PanoramaModels
import protobuf.panorama.PanoramaModels.{ImageProperty, RelativeCoordinate, TextProperty, VideoProperty}

import scala.jdk.CollectionConverters._
import scala.language.implicitConversions

object PoiProtoExample {

  val coordinate = RelativeCoordinate
    .newBuilder()
    .setX(0.1d)
    .setY(0.2d)
    .build()

  val propertyText = {
    TextProperty
      .newBuilder()
      .setText("Болты супер")
      .build()
  }

  val propertyImage1 = ImageProperty
    .newBuilder()
    .setLink("https://xxx-1")
    .build()

  val propertyImage2 = ImageProperty
    .newBuilder()
    .setLink("https://xxx-2")
    .build()

  val propertyVideo = VideoProperty
    .newBuilder()
    .setLink("https://www.youtube.com/watch?v=Gqs9voNgDUo")
    .build()

  val interiorPoi = {
    val poi = PanoramaModels.Poi
      .newBuilder()
      .setId("xxx-yyy-zzz")
      .setName("STEERING-WHEEL")
      .setCoordinate(coordinate)

    val properties = PanoramaModels.InteriorProperty
      .newBuilder()
      .setText(propertyText)
      .addAllImage(Seq(propertyImage1, propertyImage2).asJava)

    PanoramaModels.InteriorPoi
      .newBuilder()
      .setPoi(poi)
      .setProperties(properties)
      .build()
  }

  val exteriorPoi = {
    val poi = PanoramaModels.Poi
      .newBuilder()
      .setId("aaa-bbb-ccc")
      .setName("WHEEL")
      .setCoordinate(coordinate)

    val properties = PanoramaModels.ExteriorProperty
      .newBuilder()
      .setText(propertyText)
      .setYoutube(propertyVideo)
      .addAllImage(Seq(propertyImage1, propertyImage2).asJava)

    PanoramaModels.ExteriorPoi
      .newBuilder()
      .setPoi(poi)
      .setProperties(properties)
      .build()
  }

}
