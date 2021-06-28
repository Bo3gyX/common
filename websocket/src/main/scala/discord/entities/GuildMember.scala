package discord.entities

import java.time.Instant

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
