package discord.entities

import discord.Enum

abstract class StatusType(val code: String)

object StatusType extends Enum[String, StatusType] {
  case object Online    extends StatusType("online")
  case object Dnd       extends StatusType("dnd")
  case object Idle      extends StatusType("idle")
  case object Invisible extends StatusType("invisible")
  case object Offline   extends StatusType("offline")

  override def values: Seq[StatusType]                 = Seq(Online, Dnd, Idle, Invisible, Offline)
  override def find(value: String): Option[StatusType] = values.find(_.code.equalsIgnoreCase(value))
}
