package discord.gateway

import discord.entities._

object Event {
  sealed abstract class Name[+T](val value: String)

  case object Ready         extends Name[Ready]("READY")
  case object MessageCreate extends Name[Message]("MESSAGE_CREATE")

  val values: Seq[Name[Any]]                 = Seq(Ready, MessageCreate)
  def find(value: String): Option[Name[Any]] = values.find(_.value == value)
}
