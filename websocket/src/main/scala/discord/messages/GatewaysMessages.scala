package discord.messages

import discord.{entities, Opcode}

object GatewaysMessages {

  case class Hello(payload: entities.Hello)
    extends GatewaysMessage.Op(Opcode.Hello)
    with GatewaysMessage.Payload[entities.Hello]

  case class Heartbeat(payload: Int) extends GatewaysMessage.Op(Opcode.Heartbeat) with GatewaysMessage.Payload[Int]

  case object HeartbeatAck extends GatewaysMessage.Op(Opcode.HeartbeatAck) with GatewaysMessage.Payload.Empty

  case class Identify(payload: entities.Identify)
    extends GatewaysMessage.Op(Opcode.Identify)
    with GatewaysMessage.Payload[entities.Identify]

  case class MessageCreate(payload: entities.Message)
    extends GatewaysMessage.Op(Opcode.Dispatch)
    with GatewaysMessage.Payload[entities.Message]

}
