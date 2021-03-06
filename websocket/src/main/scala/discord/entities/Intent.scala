package discord.entities

import discord.Enum

import scala.annotation.tailrec

trait Intent {
  def value: Int
  def |(that: Intent): Intent
}

object Intent extends Enum[Int, Intent] {
  case object GUILDS                   extends OneIntent(1 << 0)
  case object GUILD_MEMBERS            extends OneIntent(1 << 1)
  case object GUILD_BANS               extends OneIntent(1 << 2)
  case object GUILD_EMOJIS             extends OneIntent(1 << 3)
  case object GUILD_INTEGRATIONS       extends OneIntent(1 << 4)
  case object GUILD_WEBHOOKS           extends OneIntent(1 << 5)
  case object GUILD_INVITES            extends OneIntent(1 << 6)
  case object GUILD_VOICE_STATES       extends OneIntent(1 << 7)
  case object GUILD_PRESENCES          extends OneIntent(1 << 8)
  case object GUILD_MESSAGES           extends OneIntent(1 << 9)
  case object GUILD_MESSAGE_REACTIONS  extends OneIntent(1 << 10)
  case object GUILD_MESSAGE_TYPING     extends OneIntent(1 << 11)
  case object DIRECT_MESSAGES          extends OneIntent(1 << 12)
  case object DIRECT_MESSAGE_REACTIONS extends OneIntent(1 << 13)
  case object DIRECT_MESSAGE_TYPING    extends OneIntent(1 << 14)

  sealed abstract class OneIntent(val value: Int) extends Intent {
    override def |(that: Intent): Intent = MultiIntent(Set(this, that))
  }

  case class MultiIntent(values: Set[Intent]) extends Intent {
    require(values.nonEmpty, "values can't be empty")

    override def value: Int = values.map(_.value).sum

    override def |(that: Intent): Intent = that match {
      case v: OneIntent   => copy(values + v)
      case v: MultiIntent => copy(values ++ v.values)
    }
  }

  override def values: Seq[Intent] =
    Seq(
      GUILDS,
      GUILD_MEMBERS,
      GUILD_BANS,
      GUILD_EMOJIS,
      GUILD_INTEGRATIONS,
      GUILD_WEBHOOKS,
      GUILD_INVITES,
      GUILD_VOICE_STATES,
      GUILD_PRESENCES,
      GUILD_MESSAGES,
      GUILD_MESSAGE_REACTIONS,
      GUILD_MESSAGE_TYPING,
      DIRECT_MESSAGES,
      DIRECT_MESSAGE_REACTIONS,
      DIRECT_MESSAGE_TYPING
    )

  override def find(value: Int): Option[Intent] = {
    if (value <= 0 || value > values.map(_.value).sum) None
    else values.find(_.value == value).orElse(findMulti(value))
  }

  private def findMulti(value: Int): Option[Intent] = {
    val it = values.iterator
    @tailrec
    def build(s: Int, ac: Set[Intent] = Set.empty): (Int, Set[Intent]) = {
      if (s == 0 || !it.hasNext) (s, ac)
      else {
        val next = it.next()
        val v    = next.value
        if ((s & v) == v) build(s ^ v, ac + next)
        else build(s, ac)
      }
    }
    val (remains, intents) = build(value)
    if (remains == 0) Some(MultiIntent(intents))
    else None
  }
}
