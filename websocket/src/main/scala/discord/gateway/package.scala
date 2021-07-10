package discord

import discord.entities.{Hello, Identify, Message, Ready}
import discord.gateway.Payload.Data

package object gateway extends entities.JsonConverter {
  val Hello: Payload with Data[Hello]           = Payload.operation(Operation.Hello)
  val Heartbeat: Payload with Data[Int]         = Payload.operation(Operation.Heartbeat)
  val HeartbeatAck: Payload                     = Payload.operation(Operation.HeartbeatAck)
  val Identify: Payload with Data[Identify]     = Payload.operation(Operation.Identify)
  val Ready: Payload with Data[Ready]           = Payload.event(Event.Ready)
  val MessageCreate: Payload with Data[Message] = Payload.event(Event.MessageCreate)
}
