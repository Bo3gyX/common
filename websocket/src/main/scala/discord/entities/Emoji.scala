package discord.entities

case class Emoji(
    id: Option[String],
    name: Option[String],
    roles: Seq[Role],
    user: Option[User],
    requireColons: Option[Boolean],
    managed: Option[Boolean],
    animated: Option[Boolean],
    available: Option[Boolean])
