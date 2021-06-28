package discord.entities

case class PresenceUpdate(
    status: StatusType,
    activities: Seq[Activity] = Seq.empty,
    afk: Boolean = false,
    since: Option[Long] = None)
