package websocket

import discord.Entities.{Properties, _}
import discord.messages.JsonConverter._
import discord.{Message, Messages}
import io.circe.syntax.EncoderOps
import util.Application

object Test extends Application {

  val properties: Properties = Properties("ios", "twg bot", "mac book pro")
  val shard: GuildShard      = GuildShard(0, 0)

  val presence = PresenceUpdate(
    Some(100),
    Seq(
      Activity("Watching for you", ActivityType.Watching)
    ),
    StatusType.Online,
    false
  )

  val intents: Intent =
    Intent.GUILDS |
      Intent.GUILD_MESSAGES |
      Intent.GUILD_MESSAGE_REACTIONS |
      Intent.GUILD_MESSAGE_TYPING

  val identify: Identify = Identify("xxxx", properties, intents, Some(false), Some(50), Some(shard), Some(presence))

  val msg: Message = Messages.Identify(identify)
  val json         = msg.asJson
  println(json)

  val m = json.as[Message]
  println(m)

}
