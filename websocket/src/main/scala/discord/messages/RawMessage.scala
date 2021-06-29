package discord.messages

import discord.{entities, Opcode}
import io.circe.Decoder.Result
import io.circe.{DecodingFailure, Encoder, Json}
import websocket.client.WsProcessor.WsMessage

case class RawMessage(op: Opcode, d: Option[Json], s: Option[Int], t: Option[String]) extends WsMessage

object RawMessage extends discord.entities.JsonConverter {

  def toGatewaysMessage(raw: RawMessage): Result[GatewaysMessage] = (raw.op, raw.d, raw.t) match {
    case (Opcode.Hello, Some(json), _) =>
      json.as[entities.Hello].map(GatewaysMessages.Hello)
    case (Opcode.Heartbeat, Some(json), _) =>
      json.as[Int].map(GatewaysMessages.Heartbeat)
    case (Opcode.HeartbeatAck, None, _) =>
      Right(GatewaysMessages.HeartbeatAck)
    case (Opcode.Identify, Some(json), _) =>
      json.as[entities.Identify].map(GatewaysMessages.Identify)
    case (Opcode.Dispatch, Some(json), Some("MESSAGE_CREATE")) =>
      json.as[entities.Message].map(GatewaysMessages.MessageCreate)
    case x =>
      Left(DecodingFailure(s"Unsupported message: $x", List.empty))
  }

  def toRaw[M <: GatewaysMessage](message: M)(implicit encoder: Encoder[M#Payload]): RawMessage =
    RawMessage(message.op, message.d.map(encoder(_)), message.s, message.t)
}
