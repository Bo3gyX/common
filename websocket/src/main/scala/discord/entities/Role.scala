package discord.entities

import discord.entities.Role.RoleTags

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

object Role {
  case class RoleTags(botId: Option[String], integrationId: Option[String], premiumSubscriber: Option[Boolean])
}
