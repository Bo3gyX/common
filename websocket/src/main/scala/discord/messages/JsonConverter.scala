package discord.messages

import discord._
import io.circe.Decoder.Result
import io.circe._
import io.circe.derivation.{deriveDecoder, deriveEncoder, renaming}
import io.circe.syntax.EncoderOps

trait JsonConverter extends JsonSupported with entities.JsonConverter {

  implicit val encoderOpcode: Encoder[Opcode] = new Encoder[Opcode] {
    override def apply(a: Opcode): Json = a.code.asJson
  }

  implicit val decoderOpcode: Decoder[Opcode] = new Decoder[Opcode] {

    override def apply(c: HCursor): Result[Opcode] =
      for {
        op  <- c.as[Int]
        res <- fromValid(c, Opcode.valid(op))
      } yield res
  }

  implicit val encoderRawMessage: Encoder[RawMessage] = deriveEncoder[RawMessage](renaming.snakeCase)
  implicit val decoderRawMessage: Decoder[RawMessage] = deriveDecoder[RawMessage](renaming.snakeCase)

//  implicit val encodeGatewaysMessage: Encoder[GatewaysMessage] = Encoder.instance {
//    case m: GatewaysMessages.Hello     => toJson(m)
//    case m: GatewaysMessages.Heartbeat => toJson(m)
//    case m: GatewaysMessages.Identify  => toJson(m)
//  }
//
//  implicit val decoderGatewaysMessage: Decoder[GatewaysMessage] = new Decoder[GatewaysMessage] {
//    override def apply(c: HCursor): Result[GatewaysMessage] = c.as[RawMessage].flatMap(fromJson)
//  }
//
//  def toJson[M <: GatewaysMessage](message: M)(implicit encoder: Encoder[M#Payload]): Json =
//    RawMessage.toRaw(message).asJson
//
//  def fromJson[P](raw: RawMessage): Result[GatewaysMessage] = RawMessage.toGatewaysMessage(raw)

}
