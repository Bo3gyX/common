package discord.gateway

import discord.entities._

object Operation {
  sealed abstract class Code[+T](val value: Int)

  case object Dispatch     extends Code[Nothing](0)
  case object Hello        extends Code[Hello](10)
  case object Heartbeat    extends Code[Int](1)
  case object HeartbeatAck extends Code[Nothing](11)
  case object Identify     extends Code[Identify](2)

  val values: Seq[Code[Any]]              = Seq(Dispatch, Hello, Heartbeat, HeartbeatAck, Identify)
  def find(value: Int): Option[Code[Any]] = values.find(_.value == value)
}
