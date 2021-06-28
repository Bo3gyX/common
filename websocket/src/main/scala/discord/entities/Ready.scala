package discord.entities

import io.circe.Json

case class Ready(
    v: Int,
    user: User,
    guilds: Seq[Json], //todo Json
    sessionId: String,
    shard: GuildShard,
    application: Json //todo Json
  )
