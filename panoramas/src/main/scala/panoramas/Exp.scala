package panoramas

import play.api.libs.json.{JsValue, Json}
import protobuf.Models.Photo

import scala.jdk.CollectionConverters._

object Exp {

  val str =
    """
      |{
      |    "group-id": 2970,
      |    "imagename": "i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW",
      |    "meta": {
      |        "crc64": "108619C556E1DE53",
      |        "expires-at": "Fri, 23 Oct 2020 19:12:54 GMT",
      |        "md5": "e234e321e9d451a80bfaa57c91167c07",
      |        "modification-time": 1603393974,
      |        "orig-animated": false,
      |        "orig-format": "JPEG",
      |        "orig-orientation": "0",
      |        "orig-size": {
      |            "x": 1200,
      |            "y": 750
      |        },
      |        "orig-size-bytes": 77922,
      |        "processed_by_computer_vision": false,
      |        "processed_by_computer_vision_description": "computer vision is disabled",
      |        "processing": "finished"
      |    },
      |    "sizes": {
      |        "100x100": {
      |            "height": 63,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/100x100",
      |            "width": 100
      |        },
      |        "100x75": {
      |            "height": 63,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/100x75",
      |            "width": 100
      |        },
      |        "119x30": {
      |            "height": 30,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/119x30",
      |            "width": 48
      |        },
      |        "119x30_png": {
      |            "height": 30,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/119x30_png",
      |            "width": 48
      |        },
      |        "1200x1200": {
      |            "height": 750,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/1200x1200",
      |            "width": 1200
      |        },
      |        "1200x778": {
      |            "height": 750,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/1200x778",
      |            "width": 1200
      |        },
      |        "1200x900": {
      |            "height": 750,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/1200x900",
      |            "width": 1200
      |        },
      |        "120x68": {
      |            "height": 68,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/120x68",
      |            "width": 120
      |        },
      |        "120x90": {
      |            "height": 75,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/120x90",
      |            "width": 120
      |        },
      |        "120x90_3": {
      |            "height": 90,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/120x90_3",
      |            "width": 120
      |        },
      |        "1366x768": {
      |            "height": 750,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/1366x768",
      |            "width": 1200
      |        },
      |        "154x154": {
      |            "height": 96,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/154x154",
      |            "width": 154
      |        },
      |        "155x87": {
      |            "height": 87,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/155x87",
      |            "width": 139
      |        },
      |        "1600x1600": {
      |            "height": 750,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/1600x1600",
      |            "width": 1200
      |        },
      |        "160x160": {
      |            "height": 100,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/160x160",
      |            "width": 160
      |        },
      |        "170x128": {
      |            "height": 106,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/170x128",
      |            "width": 170
      |        },
      |        "170x170": {
      |            "height": 106,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/170x170",
      |            "width": 170
      |        },
      |        "190x110": {
      |            "height": 110,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/190x110",
      |            "width": 190
      |        },
      |        "2000x2000": {
      |            "height": 750,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/2000x2000",
      |            "width": 1200
      |        },
      |        "200x150": {
      |            "height": 125,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/200x150",
      |            "width": 200
      |        },
      |        "200x200": {
      |            "height": 125,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/200x200",
      |            "width": 200
      |        },
      |        "200x90": {
      |            "height": 90,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/200x90",
      |            "width": 200
      |        },
      |        "216x121": {
      |            "height": 121,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/216x121",
      |            "width": 216
      |        },
      |        "239x60": {
      |            "height": 60,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/239x60",
      |            "width": 96
      |        },
      |        "240x180": {
      |            "height": 150,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/240x180",
      |            "width": 240
      |        },
      |        "2560x1440": {
      |            "height": 750,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/2560x1440",
      |            "width": 1200
      |        },
      |        "295x165": {
      |            "height": 165,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/295x165",
      |            "width": 295
      |        },
      |        "3000x3000": {
      |            "height": 750,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/3000x3000",
      |            "width": 1200
      |        },
      |        "300x65": {
      |            "height": 65,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/300x65",
      |            "width": 300
      |        },
      |        "302x175": {
      |            "height": 175,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/302x175",
      |            "width": 302
      |        },
      |        "311x175": {
      |            "height": 175,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/311x175",
      |            "width": 311
      |        },
      |        "3200x2400": {
      |            "height": 750,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/3200x2400",
      |            "width": 1200
      |        },
      |        "320x240": {
      |            "height": 200,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/320x240",
      |            "width": 320
      |        },
      |        "32x32": {
      |            "height": 20,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/32x32",
      |            "width": 32
      |        },
      |        "330x185": {
      |            "height": 185,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/330x185",
      |            "width": 330
      |        },
      |        "342x192": {
      |            "height": 192,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/342x192",
      |            "width": 342
      |        },
      |        "423x204": {
      |            "height": 204,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/423x204",
      |            "width": 326
      |        },
      |        "455x255": {
      |            "height": 255,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/455x255",
      |            "width": 455
      |        },
      |        "456x342": {
      |            "height": 342,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/456x342",
      |            "width": 456
      |        },
      |        "5000x5000": {
      |            "height": 750,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/5000x5000",
      |            "width": 1200
      |        },
      |        "500x400": {
      |            "height": 338,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/500x400",
      |            "width": 540
      |        },
      |        "500x400_png": {
      |            "height": 313,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/500x400_png",
      |            "width": 500
      |        },
      |        "500x500": {
      |            "height": 313,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/500x500",
      |            "width": 500
      |        },
      |        "530x340": {
      |            "height": 340,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/530x340",
      |            "width": 530
      |        },
      |        "530x624": {
      |            "height": 331,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/530x624",
      |            "width": 530
      |        },
      |        "590x230": {
      |            "height": 230,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/590x230",
      |            "width": 590
      |        },
      |        "60x45": {
      |            "height": 38,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/60x45",
      |            "width": 60
      |        },
      |        "639x241": {
      |            "height": 241,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/639x241",
      |            "width": 639
      |        },
      |        "640x480": {
      |            "height": 480,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/640x480",
      |            "width": 640
      |        },
      |        "675x380": {
      |            "height": 380,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/675x380",
      |            "width": 675
      |        },
      |        "675x450": {
      |            "height": 450,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/675x450",
      |            "width": 675
      |        },
      |        "720x540": {
      |            "height": 540,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/720x540",
      |            "width": 720
      |        },
      |        "760x570": {
      |            "height": 475,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/760x570",
      |            "width": 760
      |        },
      |        "770x1770": {
      |            "height": 481,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/770x1770",
      |            "width": 770
      |        },
      |        "88x23": {
      |            "height": 23,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/88x23",
      |            "width": 37
      |        },
      |        "88x23_png": {
      |            "height": 23,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/88x23_png",
      |            "width": 37
      |        },
      |        "900x1800": {
      |            "height": 563,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/900x1800",
      |            "width": 900
      |        },
      |        "900x675": {
      |            "height": 563,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/900x675",
      |            "width": 900
      |        },
      |        "900x675_3": {
      |            "height": 675,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/900x675_3",
      |            "width": 900
      |        },
      |        "90x90": {
      |            "height": 56,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/90x90",
      |            "width": 90
      |        },
      |        "90x90_3": {
      |            "height": 90,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/90x90_3",
      |            "width": 90
      |        },
      |        "960x720": {
      |            "height": 600,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/960x720",
      |            "width": 960
      |        },
      |        "990x557": {
      |            "height": 557,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/990x557",
      |            "width": 990
      |        },
      |        "full": {
      |            "height": 342,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/full",
      |            "width": 456
      |        },
      |        "orig": {
      |            "height": 750,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/orig",
      |            "width": 1200
      |        },
      |        "small": {
      |            "height": 90,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/small",
      |            "width": 120
      |        },
      |        "thumb_m": {
      |            "height": 210,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/thumb_m",
      |            "width": 280
      |        },
      |        "thumb_m_2x": {
      |            "height": 420,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/thumb_m_2x",
      |            "width": 560
      |        },
      |        "thumb_s": {
      |            "height": 154,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/thumb_s",
      |            "width": 205
      |        },
      |        "thumb_s_2x": {
      |            "height": 308,
      |            "path": "/get-autoru/2970/i6xhIsX6zzlVoj45W6JhXEKIEUEY7L5tW/thumb_s_2x",
      |            "width": 410
      |        }
      |    }
      |}
      |""".stripMargin

  case class Size(alias: String, width: Int, height: Int, path: String)

  val json = Json.parse(str)

  val sizes = (json \ "sizes").as[Map[String, JsValue]].map {
    case (alias, value) =>
      val width  = (value \ "width").as[Int]
      val height = (value \ "height").as[Int]
      val path   = (value \ "path").as[String]
      Size(alias, width, height, path)
  }

  //  println(sizes)

  def buildPhoto(value: String, map: Map[String, String] = Map.empty): Photo = {
    Photo
      .newBuilder()
      .setOrigin(value)
      .putAllSizes(map.asJava)
      .build()
  }

  val photos1 = Seq(
    buildPhoto("aaa1", Map("320x240" -> "aaaa")),
    buildPhoto("bbb", Map("320x240" -> "bbbb", "1200x900" -> "cccc")),
    buildPhoto("ccc", Map("320x240" -> "cccc", "1200x900" -> "dddd"))
  )

  val photos2 = Seq(
    buildPhoto("aaa", Map("320x240" -> "aaaaa", "1200x900" -> "cccc")),
    buildPhoto("bbb", Map("320x240" -> "bbbb", "1200x900" -> "cccc")),
    buildPhoto("ccc", Map("320x240" -> "cccc", "1200x900" -> "dddd"))
  )

  val res = photos1.diff(photos2)

  println(res)
}
