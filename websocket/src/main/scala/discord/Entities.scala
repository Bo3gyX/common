package discord

import io.circe.Json

import java.time.Instant
import scala.annotation.tailrec

object Entities {

  case class Hello(heartbeatInterval: Int)

  case class Identify(
      token: String,
      properties: Properties,
      intents: Intent,
      compress: Option[Boolean] = None,
      largeThreshold: Option[Int] = None,
      shard: Option[GuildShard] = None,
      presence: Option[PresenceUpdate] = None)

  case class Properties(os: String, browser: String, device: String)
  case class GuildShard(id: Int, num: Int)

  case class PresenceUpdate(
      status: StatusType,
      activities: Seq[Activity] = Seq.empty,
      afk: Boolean = false,
      since: Option[Long] = None)

  case class Activity(name: String, typeActivity: ActivityType)

  abstract class ActivityType(val code: Int)

  object ActivityType extends Enum[Int, ActivityType] {
    case object Game      extends ActivityType(0)
    case object Streaming extends ActivityType(1)
    case object Listening extends ActivityType(2)
    case object Watching  extends ActivityType(3)
    case object Custom    extends ActivityType(4)
    case object Competing extends ActivityType(5)

    def values: Seq[ActivityType]              = Seq(Game, Streaming, Listening, Watching, Custom, Competing)
    def find(value: Int): Option[ActivityType] = values.find(_.code == value)
  }

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

  case class Ready(
      v: Int,
      user: Entities.User,
      guilds: Seq[Json], //todo Json
      sessionId: String,
      shard: GuildShard,
      application: Json //todo Json
    )

  case class User(
      id: String,
      username: String,
      discriminator: String,
      avatar: Option[String],
      bot: Option[Boolean],
      system: Option[Boolean],
      mfaEnabled: Option[Boolean],
      locale: Option[String],
      verified: Option[Boolean],
      email: Option[String],
      flags: Option[Int],
      premiumType: Option[Int],
      publicFlags: Option[Int])

  case class GuildMember(
      user: Option[User],
      nick: Option[String],
      roles: Seq[String],
      joinedAt: Instant,
      premium_since: Option[Instant],
      deaf: Boolean,
      mute: Boolean,
      pending: Option[Boolean],
      permissions: Option[String])

  case class Role(
      id: String,
      name: String,
      color: Int,
      hoist: Boolean,
      position: Int,
      permissions: String,
      managed: Boolean,
      mentionable: Boolean,
      tags: Option[RoleTags])

  case class RoleTags(botId: Option[String], integrationId: Option[String], premiumSubscriber: Option[Boolean])

  case class ChannelMention(id: String, guildId: String, `type`: Int, name: String)

  case class Attachment(
      id: String,
      filename: String,
      contentType: Option[String],
      size: Int, //integer	size of file in bytes
      url: String,
      proxyUrl: String,
      height: Option[Int],
      width: Option[Int])

  case class Embed(
      title: Option[String],
      `type`: Option[EmbedType],
      description: Option[String],
      url: Option[String],
      timestamp: Option[Instant],
      color: Option[Int],
      footer: Option[EmbedFooter],
      image: Option[EmbedImage],
      thumbnail: Option[EmbedThumbnail],
      video: Option[EmbedVideo],
      provider: Option[EmbedProvider],
      author: Option[EmbedAuthor],
      fields: Option[EmbedField])

  sealed abstract class EmbedType(val value: String)

  object EmbedType extends Enum[String, EmbedType] {
    case object Rich    extends EmbedType("rich")
    case object Image   extends EmbedType("image")
    case object Video   extends EmbedType("video")
    case object Gifv    extends EmbedType("gifv")
    case object Article extends EmbedType("article")
    case object Link    extends EmbedType("link")

    override def values: Seq[EmbedType]                 = Seq(Rich, Image, Video, Gifv, Article, Link)
    override def find(value: String): Option[EmbedType] = values.find(_.value == value)
  }

  case class EmbedFooter(text: String, iconUrl: Option[String], proxyIconUrl: Option[String])
  case class EmbedImage(url: Option[String], proxyUrl: Option[String], height: Option[Int], width: Option[Int])
  case class EmbedThumbnail(url: Option[String], proxyUrl: Option[String], height: Option[Int], width: Option[Int])
  case class EmbedVideo(url: Option[String], proxyUrl: Option[String], height: Option[Int], width: Option[Int])
  case class EmbedProvider(name: Option[String], url: Option[String])

  case class EmbedAuthor(
      name: Option[String],
      url: Option[String],
      iconUrl: Option[String],
      proxyIconUrl: Option[String])

  case class EmbedField(name: String, value: String, inline: Option[Boolean])

  case class Reaction(count: Int, me: Boolean, emoji: Emoji)

  case class Emoji(
      id: Option[String],
      name: Option[String],
      roles: Seq[Role],
      user: Option[User],
      requireColons: Option[Boolean],
      managed: Option[Boolean],
      animated: Option[Boolean],
      available: Option[Boolean])
}
