package panoramas.poi

import panoramas.poi.Converter._
import play.api.libs.json.JsValue

import scala.jdk.CollectionConverters._

trait Converter[From, To] extends Reads[From, To] with Writes[From, To]

object Converter {

  trait Reads[From, To] {
    def reads(obj: From): To
  }

  trait Writes[From, To] {
    def writes(obj: To): From
  }

  implicit def read[F, T](value: F)(implicit cnv: Reads[F, T]): T   = cnv.reads(value)
  implicit def write[F, T](value: T)(implicit cnv: Writes[F, T]): F = cnv.writes(value)

  implicit def read[F, T](value: Seq[F])(implicit cnv: Reads[F, T]): Seq[T]   = value.map(cnv.reads)
  implicit def write[F, T](value: Seq[T])(implicit cnv: Writes[F, T]): Seq[F] = value.map(cnv.writes)

  implicit def readFromJavaList[F, T](javaList: java.util.List[F])(implicit cnv: Reads[F, T]): Seq[T] = {
    javaList.asScala.map(cnv.reads).toSeq
  }

  implicit def writeToJavaList[F, T](seq: Seq[T])(implicit cnv: Writes[F, T]): java.util.List[F] =
    seq.map(cnv.writes).asJava
}
