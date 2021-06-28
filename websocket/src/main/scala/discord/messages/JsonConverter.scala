package discord.messages

import discord._
import io.circe.Decoder.Result
import io.circe._
import io.circe.derivation.{deriveDecoder, deriveEncoder, renaming}
import io.circe.syntax.EncoderOps
import websocket.client.WsProcessor.WsMessage

import scala.language.implicitConversions

object JsonConverter extends discord.entities.JsonConverter with JsonSupported {

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

  case class RawMessage(op: Opcode, d: Option[Json], s: Option[Int], t: Option[String]) extends WsMessage

  object RawMessage {

    def toCommunication(raw: RawMessage): Result[GatewaysMessage] = (raw.op, raw.d) match {
      case (Opcode.Hello, Some(json))     => json.as[entities.Hello].map(GatewaysMessages.Hello)
      case (Opcode.Heartbeat, Some(json)) => json.as[Int].map(GatewaysMessages.Heartbeat)
      case (Opcode.HeartbeatAck, None)    => Right(GatewaysMessages.HeartbeatAck)
      case (Opcode.Identify, Some(json))  => json.as[entities.Identify].map(GatewaysMessages.Identify)
      case x                              => Left(DecodingFailure(s"Unsupported message: $x", List.empty))
    }

    def toRaw[M <: GatewaysMessage](message: M)(implicit encoder: Encoder[M#Payload]): RawMessage =
      RawMessage(message.op, message.d.map(encoder(_)), message.s, message.t)
  }

  implicit val encoderRawMessage: Encoder[RawMessage] = deriveEncoder[RawMessage](renaming.snakeCase)
  implicit val decoderRawMessage: Decoder[RawMessage] = deriveDecoder[RawMessage](renaming.snakeCase)

  implicit val encodeCommunication: Encoder[GatewaysMessage] = Encoder.instance {
    case m: GatewaysMessages.Hello     => toJson(m)
    case m: GatewaysMessages.Heartbeat => toJson(m)
    case m: GatewaysMessages.Identify  => toJson(m)
  }

  implicit val decoderCommunication: Decoder[GatewaysMessage] = new Decoder[GatewaysMessage] {
    override def apply(c: HCursor): Result[GatewaysMessage] = c.as[RawMessage].flatMap(fromJson)
  }

  def toJson[M <: GatewaysMessage](message: M)(implicit encoder: Encoder[M#Payload]): Json =
    RawMessage.toRaw(message).asJson

  def fromJson[P](raw: RawMessage): Result[GatewaysMessage] = RawMessage.toCommunication(raw)

}
