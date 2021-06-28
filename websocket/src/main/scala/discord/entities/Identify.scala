package discord.entities

case class Identify(
    token: String,
    properties: Properties,
    intents: Intent,
    compress: Option[Boolean] = None,
    largeThreshold: Option[Int] = None,
    shard: Option[GuildShard] = None,
    presence: Option[PresenceUpdate] = None)
