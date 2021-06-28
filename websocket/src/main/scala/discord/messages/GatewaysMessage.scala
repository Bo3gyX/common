package discord.messages

import discord.Opcode

trait GatewaysMessage {
  type Op <: Opcode
  type Payload
  def op: Op
  def d: Option[Payload]
  def s: Option[Int]
  def t: Option[String]
}

object GatewaysMessage {

  abstract class Op[Op0 <: Opcode](val op: Op0) extends GatewaysMessage {
    override type Op = Op0
    override def s: Option[Int]    = None
    override def t: Option[String] = None
  }

  trait Payload[T] { self: GatewaysMessage =>
    override type Payload = T
    def payload: T
    override def d: Option[T] = Some(payload)
  }

  object Payload {

    trait Empty extends { self: GatewaysMessage =>
      override type Payload = Nothing
      override def d: Option[Nothing] = None
    }
  }
}
