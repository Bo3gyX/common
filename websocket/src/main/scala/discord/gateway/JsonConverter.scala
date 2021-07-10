package discord.gateway

import io.circe.Decoder.Result
import io.circe.derivation.renaming
import io.circe.syntax.EncoderOps
import io.circe._

trait JsonConverter extends discord.entities.JsonConverter {

  implicit val codecOperationCode: Codec[Operation.Code[Any]] = new Codec[Operation.Code[Any]] {

    override def apply(c: HCursor): Result[Operation.Code[Any]] =
      for {
        value <- c.as[Int]
        code  <- Operation.find(value).toRight(DecodingFailure(s"Undefined code $value", c.history))
      } yield code

    override def apply(a: Operation.Code[Any]): Json = a.value.asJson
  }

  implicit val codeEventName: Codec[Event.Name[Any]] = new Codec[Event.Name[Any]] {

    override def apply(c: HCursor): Result[Event.Name[Any]] =
      for {
        value <- c.as[String]
        name  <- Event.find(value).toRight(DecodingFailure(s"Undefined name $value", c.history))
      } yield name

    override def apply(a: Event.Name[Any]): Json = a.value.asJson
  }

  implicit val codecPayload: Codec[Payload] = derivation.deriveCodec[Payload](renaming.snakeCase)
}
