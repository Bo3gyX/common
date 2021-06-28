package discord.entities

import discord.Enum
import discord.entities.Message._
import io.circe.Json

import java.time.Instant

case class Message(
    id: String,
    channelId: String,
    guildId: Option[String],
    author: User,
    member: Option[GuildMember],
    content: String,
    timestamp: Instant,
    editedTimestamp: Instant,
    tts: Boolean,
    mentionEveryone: Boolean,
    mentions: Seq[User],
    mentionRoles: Seq[Role],
    mentionChannels: Seq[ChannelMention],
    attachments: Seq[Attachment],
    embeds: Seq[Embed],
    reactions: Seq[Reaction],
    nonce: Option[String],
    pinned: Boolean,
    webhookId: Option[String],
    `type`: MessageType,
    activity: Option[MessageActivity],
    application: Option[Json], //todo Json
    applicationId: Option[String],
    messageReference: Option[MessageReference],
    flags: Option[Int],
    stickers: Seq[Json], //todo Json
    referencedMessage: Option[Message],
    interaction: Option[Json], //todo Json
    thread: Option[Json], //todo Json
    components: Seq[Json] //todo Json
  )

object Message {

  sealed abstract class MessageType(val value: Int)

  object MessageType extends Enum[Int, MessageType] {
    case object DEFAULT                                      extends MessageType(0)
    case object RECIPIENT_ADD                                extends MessageType(1)
    case object RECIPIENT_REMOVE                             extends MessageType(2)
    case object CALL                                         extends MessageType(3)
    case object CHANNEL_NAME_CHANGE                          extends MessageType(4)
    case object CHANNEL_ICON_CHANGE                          extends MessageType(5)
    case object CHANNEL_PINNED_MESSAGE                       extends MessageType(6)
    case object GUILD_MEMBER_JOIN                            extends MessageType(7)
    case object USER_PREMIUM_GUILD_SUBSCRIPTION              extends MessageType(8)
    case object USER_PREMIUM_GUILD_SUBSCRIPTION_TIER_1       extends MessageType(9)
    case object USER_PREMIUM_GUILD_SUBSCRIPTION_TIER_2       extends MessageType(10)
    case object USER_PREMIUM_GUILD_SUBSCRIPTION_TIER_3       extends MessageType(11)
    case object CHANNEL_FOLLOW_ADD                           extends MessageType(12)
    case object GUILD_DISCOVERY_DISQUALIFIED                 extends MessageType(14)
    case object GUILD_DISCOVERY_REQUALIFIED                  extends MessageType(15)
    case object GUILD_DISCOVERY_GRACE_PERIOD_INITIAL_WARNING extends MessageType(16)
    case object GUILD_DISCOVERY_GRACE_PERIOD_FINAL_WARNING   extends MessageType(17)
    case object THREAD_CREATED                               extends MessageType(18)
    case object REPLY                                        extends MessageType(19)
    case object APPLICATION_COMMAND                          extends MessageType(20)
    case object THREAD_STARTER_MESSAGE                       extends MessageType(21)
    case object GUILD_INVITE_REMINDER                        extends MessageType(22)

    override def values: Seq[MessageType] =
      Seq(
        DEFAULT,
        RECIPIENT_ADD,
        RECIPIENT_REMOVE,
        CALL,
        CHANNEL_NAME_CHANGE,
        CHANNEL_ICON_CHANGE,
        CHANNEL_PINNED_MESSAGE,
        GUILD_MEMBER_JOIN,
        USER_PREMIUM_GUILD_SUBSCRIPTION,
        USER_PREMIUM_GUILD_SUBSCRIPTION_TIER_1,
        USER_PREMIUM_GUILD_SUBSCRIPTION_TIER_2,
        USER_PREMIUM_GUILD_SUBSCRIPTION_TIER_3,
        CHANNEL_FOLLOW_ADD,
        GUILD_DISCOVERY_DISQUALIFIED,
        GUILD_DISCOVERY_REQUALIFIED,
        GUILD_DISCOVERY_GRACE_PERIOD_INITIAL_WARNING,
        GUILD_DISCOVERY_GRACE_PERIOD_FINAL_WARNING,
        THREAD_CREATED,
        REPLY,
        APPLICATION_COMMAND,
        THREAD_STARTER_MESSAGE,
        GUILD_INVITE_REMINDER
      )

    override def find(value: Int): Option[MessageType] = values.find(_.value == value)
  }

  case class MessageActivity(`type`: MessageActivityType, partyId: Option[String])

  sealed abstract class MessageActivityType(val value: Int)

  object MessageActivityType extends Enum[Int, MessageActivityType] {
    case object JOIN         extends MessageActivityType(1)
    case object SPECTATE     extends MessageActivityType(2)
    case object LISTEN       extends MessageActivityType(3)
    case object JOIN_REQUEST extends MessageActivityType(5)

    override def values: Seq[MessageActivityType]              = Seq(JOIN, SPECTATE, LISTEN, JOIN_REQUEST)
    override def find(value: Int): Option[MessageActivityType] = values.find(_.value == value)
  }

  case class MessageReference(
      messageId: Option[String],
      channelId: Option[String],
      guildId: Option[String],
      failIfNotExists: Option[Boolean])
}
