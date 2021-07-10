package discord.gateway

import io.circe.{Codec, Decoder, Encoder, Json}
import io.circe.syntax.EncoderOps
import util.Logger

import scala.reflect.ClassTag

case class Payload(op: Operation.Code[_], d: Option[Json], s: Option[Int], t: Option[Event.Name[_]])

object Payload {

  trait Data[T] extends Logger { this: Payload =>
    def codec: Codec[T]

    def apply(payload: T): Payload =
      Payload(op, Some(payload.asJson(codec)), None, t)

    def unapply(payload: Payload)(implicit ct: ClassTag[T]): Option[T] = {
      def decode = payload.d.map(_.as[T](codec)).flatMap {
        case Right(v) => Some(v)
        case Left(err) =>
          log.error(s"$ct: ${payload.op}/${payload.t}: parse error", err)
          None
      }

      if (payload.op == op && payload.t == t) {
        log.info(s"$ct: ${payload.op}/${payload.t}: try decode")
        decode
      } else {
        log.info(s"$ct: ${payload.op}/${payload.t}: skip")
        None
      }
    }

  }

  def apply(op: Operation.Code[Nothing], ent: Option[Event.Name[Nothing]]): Payload = Payload(op, None, None, ent)

  def apply[T: Decoder: Encoder](op: Operation.Code[T], ent: Option[Event.Name[T]]): Payload with Payload.Data[T] =
    new Payload(op, None, None, ent) with Payload.Data[T] {

      override def codec: Codec[T] = {
        val decoder = implicitly[Decoder[T]]
        val encoder = implicitly[Encoder[T]]
        Codec.from(decoder, encoder)
      }
    }

  def operation(op: Operation.Code[Nothing]): Payload                             = apply(op, None)
  def operation[T: Decoder: Encoder](op: Operation.Code[T]): Payload with Data[T] = apply[T](op, None)
  def event(ent: Event.Name[Nothing]): Payload                                    = apply(Operation.Dispatch, Some(ent))
  def event[T: Decoder: Encoder](ent: Event.Name[T]): Payload with Data[T]        = apply[T](Operation.Dispatch, Some(ent))
}
