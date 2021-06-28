package discord.entities

import discord.Enum
import discord.entities.Activity.ActivityType

case class Activity(name: String, typeActivity: ActivityType)

object Activity {

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
}
