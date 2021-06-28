package discord

import cats.data.Validated
import io.circe.derivation.{deriveDecoder, deriveEncoder, renaming}
import io.circe.{Decoder, DecodingFailure, Encoder, HCursor}

import scala.reflect.ClassTag

trait JsonSupported {

  def fromValid[V](c: HCursor, valid: Validated[Throwable, V]): Decoder.Result[V] =
    valid.leftMap(DecodingFailure.fromThrowable(_, c.history)).toEither
}

object JsonSupported extends JsonSupported
