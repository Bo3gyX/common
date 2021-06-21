package discord.messages

import discord.{Entities, _}
import io.circe.Decoder.Result
import io.circe._
import io.circe.derivation.{deriveDecoder, deriveEncoder, renaming}
import io.circe.syntax.EncoderOps

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

  case class RawMessage(op: Opcode, d: Option[Json], s: Option[Int], t: Option[String])
  implicit val encoderRawMessage: Encoder[RawMessage] = deriveEncoder[RawMessage](renaming.snakeCase)
  implicit val decoderRawMessage: Decoder[RawMessage] = deriveDecoder[RawMessage](renaming.snakeCase)

  implicit val encodeMessage: Encoder[Message] = Encoder.instance {
    case m: Messages.Hello     => toJson(m)
    case m: Messages.Heartbeat => toJson(m)
    case m: Messages.Identify  => toJson(m)
  }

  implicit val decoderMessage: Decoder[Message] = new Decoder[Message] {
    override def apply(c: HCursor): Result[Message] = c.as[RawMessage].flatMap(fromJson)
  }

  def toJson(message: Message)(implicit encoder: Encoder[message.Payload]): Json =
    RawMessage(message.op, message.d.map(encoder(_)), message.s, message.t).asJson

  def fromJson[P](raw: RawMessage): Result[Message] = {
    raw match {
      case RawMessage(Opcode.Hello, Some(json), _, _) =>
        json.as[Entities.Hello].map(Messages.Hello)
      case RawMessage(Opcode.Heartbeat, Some(json), _, _) =>
        json.as[Int].map(Messages.Heartbeat)
      case RawMessage(Opcode.HeartbeatAck, None, _, _) =>
        Right(Messages.HeartbeatAck)
      case RawMessage(Opcode.Identify, Some(json), _, _) =>
        json.as[Entities.Identify].map(Messages.Identify)

    }
  }

}
