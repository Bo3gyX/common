package discord.messages

import discord.{Entities, _}
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

    def toCommunication(raw: RawMessage): Result[Communication] = (raw.op, raw.d) match {
      case (Opcode.Hello, Some(json))     => json.as[Entities.Hello].map(Messages.Hello)
      case (Opcode.Heartbeat, Some(json)) => json.as[Int].map(Messages.Heartbeat)
      case (Opcode.HeartbeatAck, None)    => Right(Messages.HeartbeatAck)
      case (Opcode.Identify, Some(json))  => json.as[Entities.Identify].map(Messages.Identify)
      case x                              => Left(DecodingFailure(s"Unsupported message: $x", List.empty))
    }

    def toRaw[M <: Communication](message: M)(implicit encoder: Encoder[M#Payload]): RawMessage =
      RawMessage(message.op, message.d.map(encoder(_)), message.s, message.t)
  }

  implicit val encoderRawMessage: Encoder[RawMessage] = deriveEncoder[RawMessage](renaming.snakeCase)
  implicit val decoderRawMessage: Decoder[RawMessage] = deriveDecoder[RawMessage](renaming.snakeCase)

  implicit val encodeCommunication: Encoder[Communication] = Encoder.instance {
    case m: Messages.Hello     => toJson(m)
    case m: Messages.Heartbeat => toJson(m)
    case m: Messages.Identify  => toJson(m)
  }

  implicit val decoderCommunication: Decoder[Communication] = new Decoder[Communication] {
    override def apply(c: HCursor): Result[Communication] = c.as[RawMessage].flatMap(fromJson)
  }

  def toJson[M <: Communication](message: M)(implicit encoder: Encoder[M#Payload]): Json =
    RawMessage.toRaw(message).asJson

  def fromJson[P](raw: RawMessage): Result[Communication] = RawMessage.toCommunication(raw)

}
